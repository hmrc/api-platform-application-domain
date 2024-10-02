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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access

case class ApplicationWithSubscriptions(
    details: CoreApplication,
    collaborators: Set[Collaborator],
    subscriptions: Set[ApiIdentifier]
  ) extends HasEnvironment with HasState with HasAccess with HasCollaborators {
  lazy val id       = details.id
  lazy val name     = details.name
  lazy val clientId = details.clientId

  private[core] lazy val deployedTo = details.deployedTo
  lazy val state                    = details.state
  lazy val access                   = details.access

  // Assist with nesting
  import monocle.syntax.all._
  def modify(fn: CoreApplication => CoreApplication)                                      = this.focus(_.details).modify(fn)
  def withState(newState: ApplicationState): ApplicationWithSubscriptions                 = this.focus(_.details.state).replace(newState)
  def modifyState(fn: ApplicationState => ApplicationState): ApplicationWithSubscriptions = this.focus(_.details.state).modify(fn)
  def withAccess(newAccess: Access): ApplicationWithSubscriptions                         = this.focus(_.details.access).replace(newAccess)
  def modifyAccess(fn: Access => Access): ApplicationWithSubscriptions                    = this.focus(_.details.access).modify(fn)

  def modifyStdAccess(fn: Access.Standard => Access.Standard): ApplicationWithSubscriptions = this.access match {
    case std: Access.Standard => withAccess(fn(std))
    case _                    => this
  }
}

object ApplicationWithSubscriptions {
  import play.api.libs.json._

  private val transformOldResponse: OldExtendedApplicationResponse => ApplicationWithSubscriptions = (old) => {
    ApplicationWithSubscriptions(
      details = CoreApplication(
        id = old.id,
        clientId = old.clientId,
        gatewayId = old.gatewayId,
        name = ApplicationName(old.name),
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
        allowAutoDelete = old.allowAutoDelete,
        lastActionActor = ActorType.UNKNOWN
      ),
      collaborators = old.collaborators,
      subscriptions = old.subscriptions.toSet
    )
  }

  val reads: Reads[ApplicationWithSubscriptions]            = Json.reads[ApplicationWithSubscriptions].orElse(Json.reads[OldExtendedApplicationResponse].map(transformOldResponse))
  val writes: Writes[ApplicationWithSubscriptions]          = Json.writes[ApplicationWithSubscriptions]
  implicit val format: Format[ApplicationWithSubscriptions] = Format(reads, writes)
}
