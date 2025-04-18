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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import java.time.Instant

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{PrivacyPolicyLocation, TermsAndConditionsLocation}

trait HasEnvironment {
  self: { def deployedTo: Environment } =>

  def isProduction = deployedTo.isProduction
  def isSandbox    = deployedTo.isSandbox
}

trait HasAccess {
  self: { def access: Access } =>

  def isStandard   = access.isStandard
  def isPrivileged = access.isPriviledged
  def isROPC       = access.isROPC

  def privacyPolicyLocation: Option[PrivacyPolicyLocation] = access match {
    case a: Access.Standard => Some(a.privacyPolicyLocation)
    case _                  => None
  }

  def termsAndConditionsLocation: Option[TermsAndConditionsLocation] = access match {
    case a: Access.Standard => Some(a.termsAndConditionsLocation)
    case _                  => None
  }

  private val maximumNumberOfRedirectUris = 5

  def canAddRedirectUri: Boolean = access match {
    case s: Access.Standard => s.redirectUris.lengthCompare(maximumNumberOfRedirectUris) < 0
    case _                  => false
  }

  def hasResponsibleIndividual: Boolean = {
    access match {
      case Access.Standard(_, _, _, _, _, _, Some(_)) => true
      case _                                          => false
    }
  }

  def hasRedirectUri(redirectUri: LoginRedirectUri): Boolean = access match {
    case s: Access.Standard => s.redirectUris.contains(redirectUri)
    case _                  => false
  }
}

trait HasState {
  self: { def state: ApplicationState } =>

  def isInTesting                                                      = state.isInTesting
  def isPendingResponsibleIndividualVerification                       = state.isPendingResponsibleIndividualVerification
  def isPendingGatekeeperApproval                                      = state.isPendingGatekeeperApproval
  def isPendingRequesterVerification                                   = state.isPendingRequesterVerification
  def isInPreProduction                                                = state.isInPreProduction
  def isInPreProductionOrProduction                                    = state.isInProduction || state.isInPreProduction
  def isApproved                                                       = isInPreProductionOrProduction
  def isInPendingGatekeeperApprovalOrResponsibleIndividualVerification = isPendingGatekeeperApproval || isPendingResponsibleIndividualVerification
  def isInProduction                                                   = state.isInProduction
  def isDeleted                                                        = state.isDeleted
}

// AppLocking is related to whether an app is in a locked down state - typically requiring SDST to make actual changes
trait AppLocking {
  self: HasEnvironment with HasState with HasAccess =>

  def areSubscriptionsLocked: Boolean = (isProduction && !isInTesting) || !isStandard
}

case class CoreApplication(
    id: ApplicationId,
    clientId: ClientId,
    gatewayId: String,
    name: ApplicationName,
    deployedTo: Environment,
    description: Option[String],
    createdOn: Instant,
    lastAccess: Option[Instant],
    grantLength: GrantLength,
    lastAccessTokenUsage: Option[Instant],
    access: Access,
    state: ApplicationState,
    rateLimitTier: RateLimitTier,
    checkInformation: Option[CheckInformation],
    blocked: Boolean,
    ipAllowlist: IpAllowlist,
    lastActionActor: ActorType,
    deleteRestriction: DeleteRestriction
  ) extends HasEnvironment with HasState with AppLocking with HasAccess {

  def modifyAccess(fn: Access => Access) = this.copy(access = fn(this.access))

  def modifyStdAccess(fn: Access.Standard => Access.Standard) = this.access match {
    case std: Access.Standard => this.copy(access = fn(std))
    case _                    => this
  }
}

object CoreApplication {
  import play.api.libs.json._

  private case class OldCoreApplication(
      id: ApplicationId,
      clientId: ClientId,
      gatewayId: String,
      name: ApplicationName,
      deployedTo: Environment,
      description: Option[String],
      createdOn: Instant,
      lastAccess: Option[Instant],
      grantLength: GrantLength,
      lastAccessTokenUsage: Option[Instant],
      access: Access,
      state: ApplicationState,
      rateLimitTier: RateLimitTier,
      checkInformation: Option[CheckInformation],
      blocked: Boolean,
      ipAllowlist: IpAllowlist,
      lastActionActor: ActorType
    )

  private def transformOldResponse(old: OldCoreApplication): CoreApplication =
    CoreApplication(
      id = old.id,
      clientId = old.clientId,
      gatewayId = old.gatewayId,
      name = old.name,
      deployedTo = old.deployedTo,
      description = old.description,
      createdOn = old.createdOn,
      lastAccess = old.lastAccess,
      grantLength = old.grantLength,
      lastAccessTokenUsage = old.lastAccessTokenUsage,
      access = old.access,
      state = old.state,
      rateLimitTier = old.rateLimitTier,
      checkInformation = old.checkInformation,
      blocked = old.blocked,
      ipAllowlist = old.ipAllowlist,
      lastActionActor = old.lastActionActor,
      deleteRestriction = DeleteRestriction.NoRestriction
    )

  val reads: Reads[CoreApplication] = Json.reads[CoreApplication].orElse(Json.reads[OldCoreApplication].map(transformOldResponse))

  val writes: Writes[CoreApplication] = Json.writes[CoreApplication]

  implicit val format: Format[CoreApplication] = Format(reads, writes)

  implicit val nameOrdering: Ordering[CoreApplication] = Ordering.by[CoreApplication, ApplicationName](_.name)
}
