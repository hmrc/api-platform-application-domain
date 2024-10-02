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
trait HasEnvironment {
  self: { def deployedTo: Environment } =>

  lazy val isProduction = deployedTo.isProduction
  lazy val isSandbox    = deployedTo.isSandbox
}

trait HasAccess {
  self: { def access: Access } =>

  lazy val isStandard   = access.isStandard
  lazy val isPrivileged = access.isPriviledged
  lazy val isROPC       = access.isROPC
}

trait HasState {
  self: { def state: ApplicationState } =>

  lazy val isInTesting                                                      = state.isInTesting
  lazy val isPendingResponsibleIndividualVerification                       = state.isPendingResponsibleIndividualVerification
  lazy val isPendingGatekeeperApproval                                      = state.isPendingGatekeeperApproval
  lazy val isPendingRequesterVerification                                   = state.isPendingRequesterVerification
  lazy val isInPreProduction                                                = state.isInPreProduction
  lazy val isInPreProductionOrProduction                                    = state.isInPreProductionOrProduction
  lazy val isInPendingGatekeeperApprovalOrResponsibleIndividualVerification = state.isInPendingGatekeeperApprovalOrResponsibleIndividualVerification
  lazy val isInProduction                                                   = state.isInProduction
  lazy val isDeleted                                                        = state.isDeleted
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
    allowAutoDelete: Boolean,
    lastActionActor: ActorType
  ) extends HasEnvironment with HasState with HasAccess {

  def modifyAccess(fn: Access => Access) = this.copy(access = fn(this.access))

  def modifyStdAccess(fn: Access.Standard => Access.Standard) = this.access match {
    case std: Access.Standard => this.copy(access = fn(std))
    case _                    => this
  }

}

object CoreApplication {
  import play.api.libs.json._
  implicit val format: Format[CoreApplication] = Json.format[CoreApplication]
}
