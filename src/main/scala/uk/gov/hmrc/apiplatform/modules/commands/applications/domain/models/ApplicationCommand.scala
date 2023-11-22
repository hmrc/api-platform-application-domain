/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.LocalDateTime

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.play.json.Union
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{PrivacyPolicyLocation, TermsAndConditionsLocation}

sealed trait ApplicationCommand {
  // TODO - remove this at earliest opportunity
  def timestamp: LocalDateTime
}

sealed trait GatekeeperApplicationCommand extends ApplicationCommand {
  def gatekeeperUser: String
}

object ApplicationCommands {
  case class AddClientSecret(actor: Actors.AppCollaborator, name: String, id: ClientSecret.Id, hashedSecret: String, timestamp: LocalDateTime)            extends ApplicationCommand
  case class AddCollaborator(actor: Actor, collaborator: Collaborator, timestamp: LocalDateTime)                                                          extends ApplicationCommand
  case class AddRedirectUri(actor: Actor, redirectUriToAdd: RedirectUri, timestamp: LocalDateTime)                                                        extends ApplicationCommand
  case class ChangeGrantLength(gatekeeperUser: String, timestamp: LocalDateTime, grantLengthInDays: GrantLength)                                          extends GatekeeperApplicationCommand
  case class ChangeRateLimitTier(gatekeeperUser: String, timestamp: LocalDateTime, rateLimitTier: RateLimitTier)                                          extends GatekeeperApplicationCommand
  case class ChangeProductionApplicationName(gatekeeperUser: String, instigator: UserId, timestamp: LocalDateTime, newName: String)                       extends GatekeeperApplicationCommand
  case class ChangeProductionApplicationPrivacyPolicyLocation(instigator: UserId, timestamp: LocalDateTime, newLocation: PrivacyPolicyLocation)           extends ApplicationCommand
  case class ChangeProductionApplicationTermsAndConditionsLocation(instigator: UserId, timestamp: LocalDateTime, newLocation: TermsAndConditionsLocation) extends ApplicationCommand
  case class ChangeRedirectUri(actor: Actor, redirectUriToReplace: RedirectUri, redirectUri: RedirectUri, timestamp: LocalDateTime)                       extends ApplicationCommand
  case class ChangeResponsibleIndividualToSelf(instigator: UserId, timestamp: LocalDateTime, name: String, email: LaxEmailAddress)                        extends ApplicationCommand
  case class ChangeResponsibleIndividualToOther(code: String, timestamp: LocalDateTime)                                                                   extends ApplicationCommand
  case class DeclineApplicationApprovalRequest(gatekeeperUser: String, reasons: String, timestamp: LocalDateTime)                                         extends GatekeeperApplicationCommand
  case class DeclineResponsibleIndividual(code: String, timestamp: LocalDateTime)                                                                         extends ApplicationCommand
  case class DeclineResponsibleIndividualDidNotVerify(code: String, timestamp: LocalDateTime)                                                             extends ApplicationCommand
  case class DeleteApplicationByCollaborator(instigator: UserId, reasons: String, timestamp: LocalDateTime)                                               extends ApplicationCommand

  case class DeleteApplicationByGatekeeper(gatekeeperUser: String, requestedByEmailAddress: LaxEmailAddress, reasons: String, timestamp: LocalDateTime)
      extends GatekeeperApplicationCommand

  case class AllowApplicationAutoDelete(gatekeeperUser: String, reasons: String, timestamp: LocalDateTime) extends GatekeeperApplicationCommand
  case class BlockApplicationAutoDelete(gatekeeperUser: String, reasons: String, timestamp: LocalDateTime) extends GatekeeperApplicationCommand

  case class DeleteRedirectUri(actor: Actor, redirectUriToDelete: RedirectUri, timestamp: LocalDateTime)                                                extends ApplicationCommand
  case class DeleteProductionCredentialsApplication(jobId: String, reasons: String, timestamp: LocalDateTime)                                           extends ApplicationCommand
  case class DeleteUnusedApplication(jobId: String, authorisationKey: String, reasons: String, timestamp: LocalDateTime)                                extends ApplicationCommand
  case class RemoveClientSecret(actor: Actors.AppCollaborator, clientSecretId: ClientSecret.Id, timestamp: LocalDateTime)                               extends ApplicationCommand
  case class RemoveCollaborator(actor: Actor, collaborator: Collaborator, timestamp: LocalDateTime)                                                     extends ApplicationCommand
  case class SubscribeToApi(actor: Actor, apiIdentifier: ApiIdentifier, timestamp: LocalDateTime)                                                       extends ApplicationCommand
  case class UnsubscribeFromApi(actor: Actor, apiIdentifier: ApiIdentifier, timestamp: LocalDateTime)                                                   extends ApplicationCommand
  case class UpdateRedirectUris(actor: Actor, oldRedirectUris: List[RedirectUri], newRedirectUris: List[RedirectUri], timestamp: LocalDateTime)         extends ApplicationCommand
  case class VerifyResponsibleIndividual(instigator: UserId, timestamp: LocalDateTime, requesterName: String, riName: String, riEmail: LaxEmailAddress) extends ApplicationCommand

  case class ChangeIpAllowlist(actor: Actor, timestamp: LocalDateTime, required: Boolean, oldIpAllowlist: List[CidrBlock], newIpAllowlist: List[CidrBlock])
      extends ApplicationCommand
}

object ApplicationCommand {
  import ApplicationCommands._
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.LocalDateTimeFormatter._

  implicit private val addCollaboratorFormatter: OFormat[AddCollaborator]                                 = Json.format[AddCollaborator]
  implicit private val removeCollaboratorFormatter: OFormat[RemoveCollaborator]                           = Json.format[RemoveCollaborator]
  implicit private val addClientSecretFormatter: OFormat[AddClientSecret]                                 = Json.format[AddClientSecret]
  implicit private val removeClientSecretFormatter: OFormat[RemoveClientSecret]                           = Json.format[RemoveClientSecret]
  implicit private val addRedirectUriFormatter: OFormat[AddRedirectUri]                                   = Json.format[AddRedirectUri]
  implicit private val changeCollaboratorFormatter: OFormat[ChangeRedirectUri]                            = Json.format[ChangeRedirectUri]
  implicit private val deleteRedirectUriFormatter: OFormat[DeleteRedirectUri]                             = Json.format[DeleteRedirectUri]
  implicit private val allowApplicationAutoDeleteFormatter: OFormat[AllowApplicationAutoDelete]           = Json.format[AllowApplicationAutoDelete]
  implicit private val blockApplicationAutoDeleteFormatter: OFormat[BlockApplicationAutoDelete]           = Json.format[BlockApplicationAutoDelete]
  implicit private val changeGrantLengthFormatter: OFormat[ChangeGrantLength]                             = Json.format[ChangeGrantLength]
  implicit private val changeRateLimitTierFormatter: OFormat[ChangeRateLimitTier]                         = Json.format[ChangeRateLimitTier]
  implicit private val changeProductionApplicationNameFormatter: OFormat[ChangeProductionApplicationName] = Json.format[ChangeProductionApplicationName]

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
    .format
}

/*
--- Used in TPDFE ---
  *** Extends ApplicationUpdate ***
  ChangeProductionApplicationPrivacyPolicyLocation
  ChangeProductionApplicationTermsAndConditionsLocation
  ChangeResponsibleIndividualToSelf
  ChangeResponsibleIndividualToOther
  DeclineResponsibleIndividual
  RemoveClientSecret
  VerifyResponsibleIndividual
  DeleteApplicationByCollaborator

  *** Extends ApplicationCommand ***
  AddCollaborator
  RemoveCollaborator

--- Used in GKFE---
  *** Extends ApplicationCommand ***
  AddCollaborator
  RemoveCollaborator

  *** Extends Application Update ***
  ChangeProductionApplicationName
  DeleteApplicationByGatekeeper


--- Used in GKAFE ---
  *** extends GatekeeperApplicationUpdate ***
  DeclineApplicationApprovalRequest

--- Used in APJ ---
  ***  extends ApplicationUpdate ***
  DeleteUnusedApplication

  *** Extends ApplicationCommand ***
  AddCollaborator
  RemoveCollaborator


--- Used in APM ---
  ***   extends UpdateRequest ***
  AddCollaboratorRequest
  RemoveCollaboratorRequest

  ***  extends ApplicationUpdate ***
  AddCollaborator
  RemoveCollaborator
  SubscribeToApi
  UnsubscribeFromApi
  UpdateRedirectUris



 */
