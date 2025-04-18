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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.{OverrideFlag, SellResellOrDistribute}
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{PrivacyPolicyLocation, TermsAndConditionsLocation}

sealed trait ApplicationCommand {
  // TODO - remove this at earliest opportunity
  def timestamp: Instant
}

sealed trait GatekeeperMixin {
  def gatekeeperUser: String
}

sealed trait JobsMixin {
  def jobId: String
}

// No good way to classify commands but we don't want to have to deal with 50 types in one pattern match....
// Maybe this will improve in future
sealed abstract class RedirectCommand           extends ApplicationCommand
sealed abstract class LoginRedirectCommand      extends RedirectCommand
sealed abstract class PostLogoutRedirectCommand extends RedirectCommand
sealed abstract class ClientSecretCommand       extends ApplicationCommand
sealed abstract class CollaboratorCommand       extends ApplicationCommand
sealed abstract class SubscriptionCommand       extends ApplicationCommand
sealed abstract class IpAllowListCommand        extends ApplicationCommand
sealed abstract class GrantLengthCommand        extends ApplicationCommand
sealed abstract class RateLimitCommand          extends ApplicationCommand
sealed abstract class NameDescriptionCommand    extends ApplicationCommand
sealed abstract class DeleteCommand             extends ApplicationCommand
sealed abstract class PolicyCommand             extends ApplicationCommand
sealed abstract class SubmissionCommand         extends ApplicationCommand
sealed abstract class BlockCommand              extends ApplicationCommand
sealed abstract class ScopesCommand             extends ApplicationCommand

/*
   get list of available commands
   some map of handler vs command.
    2 contexts
    1). library update with new command -> in tpa need way of compile or start saying command in lib not handled in TPA so Error / wont start app
    2). command comes in... of type A what is the handler for type A
    1 was solved in past by exhaustivity check i.e. that was one way of solving this...
 */
object ApplicationCommands {
  case class AddLoginRedirectUri(actor: Actor, redirectUriToAdd: LoginRedirectUri, timestamp: Instant)                                       extends LoginRedirectCommand
  case class ChangeLoginRedirectUri(actor: Actor, redirectUriToReplace: LoginRedirectUri, redirectUri: LoginRedirectUri, timestamp: Instant) extends LoginRedirectCommand
  case class DeleteLoginRedirectUri(actor: Actor, redirectUriToDelete: LoginRedirectUri, timestamp: Instant)                                 extends LoginRedirectCommand

  case class UpdateLoginRedirectUris(actor: Actor, newRedirectUris: List[LoginRedirectUri], timestamp: Instant)
      extends LoginRedirectCommand

  case class AddPostLogoutRedirectUri(actor: Actor, redirectUriToAdd: PostLogoutRedirectUri, timestamp: Instant) extends PostLogoutRedirectCommand

  case class ChangePostLogoutRedirectUri(actor: Actor, redirectUriToReplace: PostLogoutRedirectUri, redirectUri: PostLogoutRedirectUri, timestamp: Instant)
      extends PostLogoutRedirectCommand
  case class DeletePostLogoutRedirectUri(actor: Actor, redirectUriToDelete: PostLogoutRedirectUri, timestamp: Instant) extends PostLogoutRedirectCommand

  case class UpdatePostLogoutRedirectUris(actor: Actor, newRedirectUris: List[PostLogoutRedirectUri], timestamp: Instant) extends PostLogoutRedirectCommand

//----
  case class AddClientSecret(actor: Actors.AppCollaborator, name: String, id: ClientSecret.Id, hashedSecret: String, timestamp: Instant)              extends ClientSecretCommand
  case class RemoveClientSecret(actor: Actors.AppCollaborator, clientSecretId: ClientSecret.Id, timestamp: Instant)                                   extends ClientSecretCommand
//----
  case class AddCollaborator(actor: Actor, collaborator: Collaborator, timestamp: Instant)                                                            extends CollaboratorCommand
  case class RemoveCollaborator(actor: Actor, collaborator: Collaborator, timestamp: Instant)                                                         extends CollaboratorCommand
//----
  case class SubscribeToApi(actor: Actor, apiIdentifier: ApiIdentifier, timestamp: Instant)                                                           extends SubscriptionCommand
  case class UnsubscribeFromApi(actor: Actor, apiIdentifier: ApiIdentifier, timestamp: Instant)                                                       extends SubscriptionCommand
  case class UnsubscribeFromRetiredApi(actor: Actors.Process, apiIdentifier: ApiIdentifier, timestamp: Instant)                                       extends SubscriptionCommand
//----
  case class ChangeIpAllowlist(actor: Actor, timestamp: Instant, required: Boolean, oldIpAllowlist: List[CidrBlock], newIpAllowlist: List[CidrBlock]) extends IpAllowListCommand

  case class ChangeProductionApplicationName(gatekeeperUser: String, instigator: UserId, timestamp: Instant, newName: ValidatedApplicationName) extends NameDescriptionCommand
      with GatekeeperMixin
  case class ChangeSandboxApplicationName(actor: Actors.AppCollaborator, timestamp: Instant, newName: ValidatedApplicationName)                 extends NameDescriptionCommand
  case class ChangeSandboxApplicationDescription(actor: Actors.AppCollaborator, timestamp: Instant, description: String)                        extends NameDescriptionCommand
  case class ClearSandboxApplicationDescription(actor: Actors.AppCollaborator, timestamp: Instant)                                              extends NameDescriptionCommand

  case class DeleteApplicationByGatekeeper(gatekeeperUser: String, requestedByEmailAddress: LaxEmailAddress, reasons: String, timestamp: Instant) extends DeleteCommand
      with GatekeeperMixin
  case class DeleteProductionCredentialsApplication(jobId: String, reasons: String, timestamp: Instant)                                           extends DeleteCommand with JobsMixin
  case class DeleteApplicationByCollaborator(instigator: UserId, reasons: String, timestamp: Instant)                                             extends DeleteCommand
  case class DeleteUnusedApplication(jobId: String, authorisationKey: String, reasons: String, timestamp: Instant)                                extends DeleteCommand with JobsMixin
  case class AllowApplicationDelete(gatekeeperUser: String, reasons: String, timestamp: Instant)                                                  extends DeleteCommand with GatekeeperMixin
  case class RestrictApplicationDelete(gatekeeperUser: String, reasons: String, timestamp: Instant)                                               extends DeleteCommand with GatekeeperMixin

  case class ChangeGrantLength(gatekeeperUser: String, timestamp: Instant, grantLength: GrantLength)       extends GrantLengthCommand with GatekeeperMixin
  case class ChangeRateLimitTier(gatekeeperUser: String, timestamp: Instant, rateLimitTier: RateLimitTier) extends RateLimitCommand with GatekeeperMixin

  case class ChangeResponsibleIndividualToSelf(instigator: UserId, timestamp: Instant, name: String, email: LaxEmailAddress)                      extends SubmissionCommand
  case class ChangeResponsibleIndividualToOther(code: String, timestamp: Instant)                                                                 extends SubmissionCommand
  case class VerifyResponsibleIndividual(instigator: UserId, timestamp: Instant, requesterName: String, riName: String, riEmail: LaxEmailAddress) extends SubmissionCommand
  case class DeclineResponsibleIndividual(code: String, timestamp: Instant)                                                                       extends SubmissionCommand
  case class DeclineResponsibleIndividualDidNotVerify(code: String, timestamp: Instant)                                                           extends SubmissionCommand
  case class DeclineApplicationApprovalRequest(gatekeeperUser: String, reasons: String, timestamp: Instant)                                       extends SubmissionCommand with GatekeeperMixin

  case class GrantApplicationApprovalRequest(gatekeeperUser: String, timestamp: Instant, warnings: Option[String], escalatedTo: Option[String]) extends SubmissionCommand
      with GatekeeperMixin

  case class GrantTermsOfUseApproval(gatekeeperUser: String, timestamp: Instant, reasons: String, escalatedTo: Option[String])                           extends SubmissionCommand with GatekeeperMixin
  case class ChangeApplicationSellResellOrDistribute(actor: Actors.AppCollaborator, timestamp: Instant, sellResellOrDistribute: SellResellOrDistribute)  extends SubmissionCommand
  case class SubmitApplicationApprovalRequest(actor: Actors.AppCollaborator, timestamp: Instant, requesterName: String, requesterEmail: LaxEmailAddress) extends SubmissionCommand
  case class SubmitTermsOfUseApproval(actor: Actors.AppCollaborator, timestamp: Instant, requesterName: String, requesterEmail: LaxEmailAddress)         extends SubmissionCommand
  case class ResendRequesterEmailVerification(gatekeeperUser: String, timestamp: Instant)                                                                extends SubmissionCommand with GatekeeperMixin
  case class SendTermsOfUseInvitation(gatekeeperUser: String, timestamp: Instant)                                                                        extends SubmissionCommand with GatekeeperMixin

  case class ChangeProductionApplicationTermsAndConditionsLocation(instigator: UserId, timestamp: Instant, newLocation: TermsAndConditionsLocation) extends PolicyCommand
  case class ChangeSandboxApplicationTermsAndConditionsUrl(actor: Actors.AppCollaborator, timestamp: Instant, termsAndConditionsUrl: String)        extends PolicyCommand
  case class RemoveSandboxApplicationTermsAndConditionsUrl(actor: Actors.AppCollaborator, timestamp: Instant)                                       extends PolicyCommand
  case class ChangeProductionApplicationPrivacyPolicyLocation(instigator: UserId, timestamp: Instant, newLocation: PrivacyPolicyLocation)           extends PolicyCommand
  case class ChangeSandboxApplicationPrivacyPolicyUrl(actor: Actors.AppCollaborator, timestamp: Instant, privacyPolicyUrl: String)                  extends PolicyCommand
  case class RemoveSandboxApplicationPrivacyPolicyUrl(actor: Actors.AppCollaborator, timestamp: Instant)                                            extends PolicyCommand

  case class BlockApplication(gatekeeperUser: String, timestamp: Instant)                                               extends BlockCommand with GatekeeperMixin
  case class UnblockApplication(gatekeeperUser: String, timestamp: Instant)                                             extends BlockCommand with GatekeeperMixin
  case class ChangeApplicationScopes(gatekeeperUser: String, scopes: Set[String], timestamp: Instant)                   extends ScopesCommand with GatekeeperMixin
  case class ChangeApplicationAccessOverrides(gatekeeperUser: String, overrides: Set[OverrideFlag], timestamp: Instant) extends ScopesCommand with GatekeeperMixin

}

object ApplicationCommand {
  import ApplicationCommands._

  implicit private val instantFormat: Format[Instant]                                                                       = InstantJsonFormatter.WithTimeZone.instantWithTimeZoneFormat
  implicit private val addCollaboratorFormatter: OFormat[AddCollaborator]                                                   = Json.format[AddCollaborator]
  implicit private val removeCollaboratorFormatter: OFormat[RemoveCollaborator]                                             = Json.format[RemoveCollaborator]
  implicit private val addClientSecretFormatter: OFormat[AddClientSecret]                                                   = Json.format[AddClientSecret]
  implicit private val removeClientSecretFormatter: OFormat[RemoveClientSecret]                                             = Json.format[RemoveClientSecret]
  implicit private val changeSandboxApplicationNameFormatter: OFormat[ChangeSandboxApplicationName]                         = Json.format[ChangeSandboxApplicationName]
  implicit private val changeSandboxApplicationDescriptionFormatter: OFormat[ChangeSandboxApplicationDescription]           = Json.format[ChangeSandboxApplicationDescription]
  implicit private val changeSandboxApplicationPrivacyPolicyUrlFormatter: OFormat[ChangeSandboxApplicationPrivacyPolicyUrl] = Json.format[ChangeSandboxApplicationPrivacyPolicyUrl]

  implicit private val changeSandboxApplicationTermsAndConditionsUrlFormatter: OFormat[ChangeSandboxApplicationTermsAndConditionsUrl] =
    Json.format[ChangeSandboxApplicationTermsAndConditionsUrl]
  implicit private val clearSandboxApplicationDescriptionFormatter: OFormat[ClearSandboxApplicationDescription]                       = Json.format[ClearSandboxApplicationDescription]
  implicit private val removeSandboxApplicationPrivacyPolicyUrlFormatter: OFormat[RemoveSandboxApplicationPrivacyPolicyUrl]           = Json.format[RemoveSandboxApplicationPrivacyPolicyUrl]

  implicit private val removeSandboxApplicationTermsAndConditionsUrlFormatter: OFormat[RemoveSandboxApplicationTermsAndConditionsUrl] =
    Json.format[RemoveSandboxApplicationTermsAndConditionsUrl]
  implicit private val addLoginRedirectUriFormatter: OFormat[AddLoginRedirectUri]                                                     = Json.format[AddLoginRedirectUri]
  implicit private val changeLoginCollaboratorFormatter: OFormat[ChangeLoginRedirectUri]                                              = Json.format[ChangeLoginRedirectUri]
  implicit private val deleteLoginRedirectUriFormatter: OFormat[DeleteLoginRedirectUri]                                               = Json.format[DeleteLoginRedirectUri]
  implicit private val UpdateLoginRedirectUrisFormatter: OFormat[UpdateLoginRedirectUris]                                             = Json.format[UpdateLoginRedirectUris]
  implicit private val addPostLogoutRedirectUriFormatter: OFormat[AddPostLogoutRedirectUri]                                           = Json.format[AddPostLogoutRedirectUri]
  implicit private val changePostLogoutCollaboratorFormatter: OFormat[ChangePostLogoutRedirectUri]                                    = Json.format[ChangePostLogoutRedirectUri]
  implicit private val deletePostLogoutRedirectUriFormatter: OFormat[DeletePostLogoutRedirectUri]                                     = Json.format[DeletePostLogoutRedirectUri]
  implicit private val UpdatePostLogoutRedirectUrisFormatter: OFormat[UpdatePostLogoutRedirectUris]                                   = Json.format[UpdatePostLogoutRedirectUris]

  implicit private val allowApplicationDeleteFormatter: OFormat[AllowApplicationDelete]       = Json.format[AllowApplicationDelete]
  implicit private val restrictApplicationDeleteFormatter: OFormat[RestrictApplicationDelete] = Json.format[RestrictApplicationDelete]

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
  implicit private val grantApplicationApprovalRequestFormatter: OFormat[GrantApplicationApprovalRequest]                        = Json.format[GrantApplicationApprovalRequest]

  implicit private val grantTermsOfUseApprovalFormat: OFormat[GrantTermsOfUseApproval]                                 = Json.format[GrantTermsOfUseApproval]
  implicit private val changeApplicationSellResellOrDistributeFormat: OFormat[ChangeApplicationSellResellOrDistribute] = Json.format[ChangeApplicationSellResellOrDistribute]
  implicit private val submitApplicationApprovalRequestFormat: OFormat[SubmitApplicationApprovalRequest]               = Json.format[SubmitApplicationApprovalRequest]
  implicit private val submitTermsOfUseApprovalFormat: OFormat[SubmitTermsOfUseApproval]                               = Json.format[SubmitTermsOfUseApproval]
  implicit private val resendRequesterEmailVerificationFormat: OFormat[ResendRequesterEmailVerification]               = Json.format[ResendRequesterEmailVerification]
  implicit private val sendTermsOfUseInvitationFormat: OFormat[SendTermsOfUseInvitation]                               = Json.format[SendTermsOfUseInvitation]

  implicit private val subscribeToApiFormatter: OFormat[SubscribeToApi]                       = Json.format[SubscribeToApi]
  implicit private val unsubscribeFromApiFormatter: OFormat[UnsubscribeFromApi]               = Json.format[UnsubscribeFromApi]
  implicit private val unsubscribeFromRetiredApiFormatter: OFormat[UnsubscribeFromRetiredApi] = Json.format[UnsubscribeFromRetiredApi]
  implicit private val ChangeIpAllowlistFormatter: OFormat[ChangeIpAllowlist]                 = Json.format[ChangeIpAllowlist]

  implicit private val blockApplicationFormat: OFormat[BlockApplication]                                 = Json.format[BlockApplication]
  implicit private val unblockApplicationFormat: OFormat[UnblockApplication]                             = Json.format[UnblockApplication]
  implicit private val changeApplicationScopesFormat: OFormat[ChangeApplicationScopes]                   = Json.format[ChangeApplicationScopes]
  implicit private val changeApplicationAccessOverridesFormat: OFormat[ChangeApplicationAccessOverrides] = Json.format[ChangeApplicationAccessOverrides]

  implicit val formatter: OFormat[ApplicationCommand] = Union.from[ApplicationCommand]("updateType")
    .and[AddCollaborator]("addCollaborator")
    .and[RemoveCollaborator]("removeCollaborator")
    .and[AddClientSecret]("addClientSecret")
    .and[RemoveClientSecret]("removeClientSecret")
    .and[AddLoginRedirectUri]("addRedirectUri")
    .and[ChangeLoginRedirectUri]("changeRedirectUri")
    .and[DeleteLoginRedirectUri]("deleteRedirectUri")
    .and[UpdateLoginRedirectUris]("updateRedirectUris")
    .and[AddPostLogoutRedirectUri]("addPostLogoutRedirectUri")
    .and[ChangePostLogoutRedirectUri]("changePostLogoutRedirectUri")
    .and[DeletePostLogoutRedirectUri]("deletePostLogoutRedirectUri")
    .and[UpdatePostLogoutRedirectUris]("updatePostLogoutRedirectUris")
    .and[AllowApplicationDelete]("allowApplicationDelete")
    .and[RestrictApplicationDelete]("restrictApplicationDelete")
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
    .and[GrantApplicationApprovalRequest]("grantApplicationApprovalRequest")
    .and[GrantTermsOfUseApproval]("grantTermsOfUseApproval")
    .and[ChangeApplicationSellResellOrDistribute]("changeApplicationSellResellOrDistribute")
    .and[SubmitApplicationApprovalRequest]("submitApplicationApprovalRequest")
    .and[SubmitTermsOfUseApproval]("submitTermsOfUseApproval")
    .and[ResendRequesterEmailVerification]("resendRequesterEmailVerification")
    .and[SendTermsOfUseInvitation]("sendTermsOfUseInvitation")
    .and[SubscribeToApi]("subscribeToApi")
    .and[UnsubscribeFromApi]("unsubscribeFromApi")
    .and[UnsubscribeFromRetiredApi]("unsubscribeFromRetiredApi")
    .and[VerifyResponsibleIndividual]("verifyResponsibleIndividual")
    .and[ChangeIpAllowlist]("changeIpAllowlist")
    .and[ChangeSandboxApplicationName]("changeSandboxApplicationName")
    .and[ChangeSandboxApplicationDescription]("changeSandboxApplicationDescription")
    .and[ChangeSandboxApplicationPrivacyPolicyUrl]("changeSandboxApplicationPrivacyPolicyUrl")
    .and[ChangeSandboxApplicationTermsAndConditionsUrl]("changeSandboxApplicationTermsAndConditionsUrl")
    .and[ClearSandboxApplicationDescription]("clearSandboxApplicationDescription")
    .and[RemoveSandboxApplicationPrivacyPolicyUrl]("removeSandboxApplicationPrivacyPolicyUrl")
    .and[RemoveSandboxApplicationTermsAndConditionsUrl]("removeSandboxApplicationTermsAndConditionsUrl")
    .and[BlockApplication]("blockApplication")
    .and[UnblockApplication]("unblockApplication")
    .and[ChangeApplicationScopes]("changeApplicationScopes")
    .and[ChangeApplicationAccessOverrides]("changeApplicationAccessOverrides")
    .format
}
