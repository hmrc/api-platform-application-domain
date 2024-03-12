/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import java.time.Instant

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.play.json.Union
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.services.InstantJsonFormatter

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{PrivacyPolicyLocation, TermsAndConditionsLocation}

sealed trait ApplicationCommand {
  // TODO - remove this at earliest opportunity
  def timestamp: Instant
}

sealed trait GatekeeperMixin {
  self: ApplicationCommand =>

  def gatekeeperUser: String
}

sealed trait DeleteApplicationMixin {
  self: ApplicationCommand =>
}

sealed trait RedirectUriMixin {
  self: ApplicationCommand =>
}

sealed trait ProductionMixin {
  self: ApplicationCommand =>
}

sealed trait SandboxMixin {
  self: ApplicationCommand =>
}

sealed trait ResponsibleIndividualMixin {
  self: ApplicationCommand =>
}

object ApplicationCommands {
  case class AddRedirectUri(actor: Actor, redirectUriToAdd: RedirectUri, timestamp: Instant)                                  extends ApplicationCommand with RedirectUriMixin
  case class ChangeRedirectUri(actor: Actor, redirectUriToReplace: RedirectUri, redirectUri: RedirectUri, timestamp: Instant) extends ApplicationCommand with RedirectUriMixin
  case class DeleteRedirectUri(actor: Actor, redirectUriToDelete: RedirectUri, timestamp: Instant)                            extends ApplicationCommand with RedirectUriMixin

  case class UpdateRedirectUris(actor: Actor, oldRedirectUris: List[RedirectUri], newRedirectUris: List[RedirectUri], timestamp: Instant) extends ApplicationCommand
      with RedirectUriMixin

  case class AddClientSecret(actor: Actors.AppCollaborator, name: String, id: ClientSecret.Id, hashedSecret: String, timestamp: Instant) extends ApplicationCommand
  case class RemoveClientSecret(actor: Actors.AppCollaborator, clientSecretId: ClientSecret.Id, timestamp: Instant)                      extends ApplicationCommand

  case class AddCollaborator(actor: Actor, collaborator: Collaborator, timestamp: Instant)    extends ApplicationCommand
  case class RemoveCollaborator(actor: Actor, collaborator: Collaborator, timestamp: Instant) extends ApplicationCommand

  case class ChangeGrantLength(gatekeeperUser: String, timestamp: Instant, grantLengthInDays: GrantLength)                                          extends ApplicationCommand with GatekeeperMixin
  case class ChangeRateLimitTier(gatekeeperUser: String, timestamp: Instant, rateLimitTier: RateLimitTier)                                          extends ApplicationCommand with GatekeeperMixin
  case class ChangeProductionApplicationName(gatekeeperUser: String, instigator: UserId, timestamp: Instant, newName: String)                       extends ApplicationCommand with GatekeeperMixin with ProductionMixin
  case class ChangeProductionApplicationPrivacyPolicyLocation(instigator: UserId, timestamp: Instant, newLocation: PrivacyPolicyLocation)           extends ApplicationCommand with ProductionMixin
  case class ChangeProductionApplicationTermsAndConditionsLocation(instigator: UserId, timestamp: Instant, newLocation: TermsAndConditionsLocation) extends ApplicationCommand with ProductionMixin

  case class ChangeResponsibleIndividualToSelf(instigator: UserId, timestamp: Instant, name: String, email: LaxEmailAddress)                        extends ApplicationCommand
      with ResponsibleIndividualMixin
  case class ChangeResponsibleIndividualToOther(code: String, timestamp: Instant)                                                                   extends ApplicationCommand with ResponsibleIndividualMixin

  case class ChangeSandboxApplicationName(actor: Actors.AppCollaborator, timestamp: Instant, newName: String)                      extends ApplicationCommand with SandboxMixin
  case class ChangeSandboxApplicationDescription(actor: Actors.AppCollaborator, timestamp: Instant, description: String)           extends ApplicationCommand with SandboxMixin
  case class ChangeSandboxApplicationPrivacyPolicyUrl(actor: Actors.AppCollaborator, timestamp: Instant, privacyPolicyUrl: String) extends ApplicationCommand with SandboxMixin

  case class ChangeSandboxApplicationTermsAndConditionsUrl(actor: Actors.AppCollaborator, timestamp: Instant, termsAndConditionsUrl: String) extends ApplicationCommand
      with SandboxMixin
  case class ClearSandboxApplicationDescription(actor: Actors.AppCollaborator, timestamp: Instant)                                           extends ApplicationCommand with SandboxMixin
  case class RemoveSandboxApplicationPrivacyPolicyUrl(actor: Actors.AppCollaborator, timestamp: Instant)                                     extends ApplicationCommand with SandboxMixin
  case class RemoveSandboxApplicationTermsAndConditionsUrl(actor: Actors.AppCollaborator, timestamp: Instant)                                extends ApplicationCommand with SandboxMixin

  case class DeclineApplicationApprovalRequest(gatekeeperUser: String, reasons: String, timestamp: Instant) extends ApplicationCommand with GatekeeperMixin
  case class DeclineResponsibleIndividual(code: String, timestamp: Instant)                                 extends ApplicationCommand with ResponsibleIndividualMixin
  case class DeclineResponsibleIndividualDidNotVerify(code: String, timestamp: Instant)                     extends ApplicationCommand with ResponsibleIndividualMixin

  case class AllowApplicationAutoDelete(gatekeeperUser: String, reasons: String, timestamp: Instant) extends ApplicationCommand with GatekeeperMixin with DeleteApplicationMixin
  case class BlockApplicationAutoDelete(gatekeeperUser: String, reasons: String, timestamp: Instant) extends ApplicationCommand with GatekeeperMixin with DeleteApplicationMixin

  case class DeleteApplicationByCollaborator(instigator: UserId, reasons: String, timestamp: Instant)              extends ApplicationCommand with DeleteApplicationMixin
  case class DeleteProductionCredentialsApplication(jobId: String, reasons: String, timestamp: Instant)            extends ApplicationCommand with DeleteApplicationMixin
  case class DeleteUnusedApplication(jobId: String, authorisationKey: String, reasons: String, timestamp: Instant) extends ApplicationCommand with DeleteApplicationMixin

  case class DeleteApplicationByGatekeeper(gatekeeperUser: String, requestedByEmailAddress: LaxEmailAddress, reasons: String, timestamp: Instant)
      extends ApplicationCommand with GatekeeperMixin with DeleteApplicationMixin

  case class SubscribeToApi(actor: Actor, apiIdentifier: ApiIdentifier, timestamp: Instant)     extends ApplicationCommand
  case class UnsubscribeFromApi(actor: Actor, apiIdentifier: ApiIdentifier, timestamp: Instant) extends ApplicationCommand

  case class VerifyResponsibleIndividual(instigator: UserId, timestamp: Instant, requesterName: String, riName: String, riEmail: LaxEmailAddress) extends ApplicationCommand
      with ResponsibleIndividualMixin

  case class ChangeIpAllowlist(actor: Actor, timestamp: Instant, required: Boolean, oldIpAllowlist: List[CidrBlock], newIpAllowlist: List[CidrBlock])
      extends ApplicationCommand
}

object ApplicationCommand {
  import ApplicationCommands._

  implicit private val instantFormat: Format[Instant]                                                                       = InstantJsonFormatter.WithTimeZone.instantWithTimeZoneFormat
  implicit private val addCollaboratorFormatter: OFormat[AddCollaborator]                                                   = Json.format[AddCollaborator]
  implicit private val removeCollaboratorFormatter: OFormat[RemoveCollaborator]                                             = Json.format[RemoveCollaborator]
  implicit private val addClientSecretFormatter: OFormat[AddClientSecret]                                                   = Json.format[AddClientSecret]
  implicit private val removeClientSecretFormatter: OFormat[RemoveClientSecret]                                             = Json.format[RemoveClientSecret]
  implicit private val addRedirectUriFormatter: OFormat[AddRedirectUri]                                                     = Json.format[AddRedirectUri]
  implicit private val changeSandboxApplicationNameFormatter: OFormat[ChangeSandboxApplicationName]                         = Json.format[ChangeSandboxApplicationName]
  implicit private val changeSandboxApplicationDescriptionFormatter: OFormat[ChangeSandboxApplicationDescription]           = Json.format[ChangeSandboxApplicationDescription]
  implicit private val changeSandboxApplicationPrivacyPolicyUrlFormatter: OFormat[ChangeSandboxApplicationPrivacyPolicyUrl] = Json.format[ChangeSandboxApplicationPrivacyPolicyUrl]

  implicit private val changeSandboxApplicationTermsAndConditionsUrlFormatter: OFormat[ChangeSandboxApplicationTermsAndConditionsUrl] =
    Json.format[ChangeSandboxApplicationTermsAndConditionsUrl]
  implicit private val clearSandboxApplicationDescriptionFormatter: OFormat[ClearSandboxApplicationDescription]                       = Json.format[ClearSandboxApplicationDescription]
  implicit private val removeSandboxApplicationPrivacyPolicyUrlFormatter: OFormat[RemoveSandboxApplicationPrivacyPolicyUrl]           = Json.format[RemoveSandboxApplicationPrivacyPolicyUrl]

  implicit private val removeSandboxApplicationTermsAndConditionsUrlFormatter: OFormat[RemoveSandboxApplicationTermsAndConditionsUrl] =
    Json.format[RemoveSandboxApplicationTermsAndConditionsUrl]
  implicit private val changeCollaboratorFormatter: OFormat[ChangeRedirectUri]                                                        = Json.format[ChangeRedirectUri]
  implicit private val deleteRedirectUriFormatter: OFormat[DeleteRedirectUri]                                                         = Json.format[DeleteRedirectUri]
  implicit private val allowApplicationAutoDeleteFormatter: OFormat[AllowApplicationAutoDelete]                                       = Json.format[AllowApplicationAutoDelete]
  implicit private val blockApplicationAutoDeleteFormatter: OFormat[BlockApplicationAutoDelete]                                       = Json.format[BlockApplicationAutoDelete]
  implicit private val changeGrantLengthFormatter: OFormat[ChangeGrantLength]                                                         = Json.format[ChangeGrantLength]
  implicit private val changeRateLimitTierFormatter: OFormat[ChangeRateLimitTier]                                                     = Json.format[ChangeRateLimitTier]
  implicit private val changeProductionApplicationNameFormatter: OFormat[ChangeProductionApplicationName]                             = Json.format[ChangeProductionApplicationName]

  implicit private val changePrivacyPolicyLocationFormatter: OFormat[ChangeProductionApplicationPrivacyPolicyLocation] =
    Json.format[ChangeProductionApplicationPrivacyPolicyLocation]

  implicit private val changeTermsAndConditionsLocationFormatter: OFormat[ChangeProductionApplicationTermsAndConditionsLocation] =
    Json.format[ChangeProductionApplicationTermsAndConditionsLocation]
  implicit private val changeResponsibleIndividualToSelfFormatter: OFormat[ChangeResponsibleIndividualToSelf]                    = Json.format[ChangeResponsibleIndividualToSelf]
  implicit private val changeResponsibleIndividualToOtherFormatter: OFormat[ChangeResponsibleIndividualToOther]                  = Json.format[ChangeResponsibleIndividualToOther]
  implicit private val verifyResponsibleIndividualFormatter: OFormat[VerifyResponsibleIndividual]                                = Json.format[VerifyResponsibleIndividual]
  implicit private val declineApplicationApprovalRequestFormatter: OFormat[DeclineApplicationApprovalRequest]                    = Json.format[DeclineApplicationApprovalRequest]
  implicit private val declineResponsibleIndividualFormatter: OFormat[DeclineResponsibleIndividual]                              = Json.format[DeclineResponsibleIndividual]
  implicit private val declineResponsibleIndividualDidNotVerifyFormatter: OFormat[DeclineResponsibleIndividualDidNotVerify]      = Json.format[DeclineResponsibleIndividualDidNotVerify]
  implicit private val deleteApplicationByCollaboratorFormatter: OFormat[DeleteApplicationByCollaborator]                        = Json.format[DeleteApplicationByCollaborator]
  implicit private val deleteApplicationByGatekeeperFormatter: OFormat[DeleteApplicationByGatekeeper]                            = Json.format[DeleteApplicationByGatekeeper]
  implicit private val deleteUnusedApplicationFormatter: OFormat[DeleteUnusedApplication]                                        = Json.format[DeleteUnusedApplication]
  implicit private val deleteProductionCredentialsApplicationFormatter: OFormat[DeleteProductionCredentialsApplication]          = Json.format[DeleteProductionCredentialsApplication]
  implicit private val subscribeToApiFormatter: OFormat[SubscribeToApi]                                                          = Json.format[SubscribeToApi]
  implicit private val unsubscribeFromApiFormatter: OFormat[UnsubscribeFromApi]                                                  = Json.format[UnsubscribeFromApi]
  implicit private val UpdateRedirectUrisFormatter: OFormat[UpdateRedirectUris]                                                  = Json.format[UpdateRedirectUris]
  implicit private val ChangeIpAllowlistFormatter: OFormat[ChangeIpAllowlist]                                                    = Json.format[ChangeIpAllowlist]

  implicit val formatter: OFormat[ApplicationCommand] = Union.from[ApplicationCommand]("updateType")
    .and[AddCollaborator]("addCollaborator")
    .and[RemoveCollaborator]("removeCollaborator")
    .and[AddClientSecret]("addClientSecret")
    .and[RemoveClientSecret]("removeClientSecret")
    .and[AddRedirectUri]("addRedirectUri")
    .and[ChangeRedirectUri]("changeRedirectUri")
    .and[DeleteRedirectUri]("deleteRedirectUri")
    .and[AllowApplicationAutoDelete]("allowApplicationAutoDelete")
    .and[BlockApplicationAutoDelete]("blockApplicationAutoDelete")
    .and[ChangeGrantLength]("changeGrantLength")
    .and[ChangeRateLimitTier]("changeRateLimitTier")
    .and[ChangeProductionApplicationName]("changeProductionApplicationName")
    .and[ChangeProductionApplicationPrivacyPolicyLocation]("changeProductionApplicationPrivacyPolicyLocation")
    .and[ChangeProductionApplicationTermsAndConditionsLocation]("changeProductionApplicationTermsAndConditionsLocation")
    .and[ChangeResponsibleIndividualToSelf]("changeResponsibleIndividualToSelf")
    .and[ChangeResponsibleIndividualToOther]("changeResponsibleIndividualToOther")
    .and[DeclineApplicationApprovalRequest]("declineApplicationApprovalRequest")
    .and[DeclineResponsibleIndividual]("declineResponsibleIndividual")
    .and[DeclineResponsibleIndividualDidNotVerify]("declineResponsibleIndividualDidNotVerify")
    .and[DeleteApplicationByCollaborator]("deleteApplicationByCollaborator")
    .and[DeleteApplicationByGatekeeper]("deleteApplicationByGatekeeper")
    .and[DeleteUnusedApplication]("deleteUnusedApplication")
    .and[DeleteProductionCredentialsApplication]("deleteProductionCredentialsApplication")
    .and[SubscribeToApi]("subscribeToApi")
    .and[UnsubscribeFromApi]("unsubscribeFromApi")
    .and[UpdateRedirectUris]("updateRedirectUris")
    .and[VerifyResponsibleIndividual]("verifyResponsibleIndividual")
    .and[ChangeIpAllowlist]("changeIpAllowlist")
    .and[ChangeSandboxApplicationName]("changeSandboxApplicationName")
    .and[ChangeSandboxApplicationDescription]("changeSandboxApplicationDescription")
    .and[ChangeSandboxApplicationPrivacyPolicyUrl]("changeSandboxApplicationPrivacyPolicyUrl")
    .and[ChangeSandboxApplicationTermsAndConditionsUrl]("changeSandboxApplicationTermsAndConditionsUrl")
    .and[ClearSandboxApplicationDescription]("clearSandboxApplicationDescription")
    .and[RemoveSandboxApplicationPrivacyPolicyUrl]("removeSandboxApplicationPrivacyPolicyUrl")
    .and[RemoveSandboxApplicationTermsAndConditionsUrl]("removeSandboxApplicationTermsAndConditionsUrl")
    .format
}
