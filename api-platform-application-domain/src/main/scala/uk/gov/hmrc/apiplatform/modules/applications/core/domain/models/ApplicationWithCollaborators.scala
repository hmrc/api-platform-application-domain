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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ActorType, ApiIdentifier, UserId}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access

trait HasCollaborators {
  self: { def collaborators: Set[Collaborator] } =>

  lazy val admins: Set[Collaborator] = collaborators.filter(_.isAdministrator)

  def roleFor(userId: UserId): Option[Collaborator.Role] = collaborators.find(_.userId == userId).map(_.role)
  def isCollaborator(userId: UserId): Boolean            = roleFor(userId).isDefined
}

case class ApplicationWithCollaborators(
    details: CoreApplication,
    collaborators: Set[Collaborator]
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
  def withState(newState: ApplicationState): ApplicationWithCollaborators                 = this.focus(_.details.state).replace(newState)
  def modifyState(fn: ApplicationState => ApplicationState): ApplicationWithCollaborators = this.focus(_.details.state).modify(fn)

  def modifyStdAccess(fn: Access.Standard => Access.Standard): ApplicationWithCollaborators = this.access match {
    case std: Access.Standard => withAccess(fn(std))
    case _                    => this
  }
  def withAccess(newAccess: Access): ApplicationWithCollaborators                           = this.focus(_.details.access).replace(newAccess)
  def modifyAccess(fn: Access => Access): ApplicationWithCollaborators                      = this.focus(_.details.access).modify(fn)

  def withSubscriptions(subscriptions: Set[ApiIdentifier]): ApplicationWithSubscriptions =
    ApplicationWithSubscriptions(
      this.details,
      this.collaborators,
      subscriptions
    )
}

object ApplicationWithCollaborators {
  import play.api.libs.json._

  private val transformOldResponse: OldApplicationResponse => ApplicationWithCollaborators = (old) => {
    ApplicationWithCollaborators(
      details = CoreApplication(
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
        allowAutoDelete = old.moreApplication.fold(false)(_.allowAutoDelete),
        lastActionActor = old.moreApplication.fold[ActorType](ActorType.UNKNOWN)(_.lastActionActor)
      ),
      collaborators = old.collaborators
    )
  }

  val reads: Reads[ApplicationWithCollaborators]            = Json.reads[ApplicationWithCollaborators].orElse(Json.reads[OldApplicationResponse].map(transformOldResponse))
  val writes: Writes[ApplicationWithCollaborators]          = Json.writes[ApplicationWithCollaborators]
  implicit val format: Format[ApplicationWithCollaborators] = Format(reads, writes)
}
