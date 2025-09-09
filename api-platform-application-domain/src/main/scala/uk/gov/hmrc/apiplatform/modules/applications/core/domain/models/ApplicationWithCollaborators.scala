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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._

trait HasCollaborators {
  self: { def collaborators: Set[Collaborator] } =>

  val admins: Set[Collaborator] = collaborators.filter(_.isAdministrator)

  def roleFor(email: LaxEmailAddress): Option[Collaborator.Role] = collaborators.find(_.emailAddress == email).map(_.role)

  // TODO - remove this or make private
  def roleFor(userId: UserId): Option[Collaborator.Role] = collaborators.find(_.userId == userId).map(_.role)
  def isCollaborator(userId: UserId): Boolean            = roleFor(userId).isDefined
  def isAdministrator(userId: UserId): Boolean           = roleFor(userId).fold(false)(_.isAdministrator)
}

case class ApplicationWithCollaborators(
    details: CoreApplication,
    collaborators: Set[Collaborator]
  ) extends HasEnvironment with HasState with AppLocking with HasAccess with HasCollaborators {

  // $COVERAGE-OFF$
  def id: ApplicationId     = details.id
  def name: ApplicationName = details.name
  def clientId: ClientId    = details.clientId

  def deployedTo: Environment = details.deployedTo
  def state: ApplicationState = details.state
  def access: Access          = details.access
  // $COVERAGE-ON$

  // Assist with nesting
  import monocle.syntax.all._

  def modify(fn: CoreApplication => CoreApplication): ApplicationWithCollaborators        = this.focus(_.details).modify(fn)
  def withState(newState: ApplicationState): ApplicationWithCollaborators                 = this.focus(_.details.state).replace(newState)
  def modifyState(fn: ApplicationState => ApplicationState): ApplicationWithCollaborators = this.focus(_.details.state).modify(fn)

  def modifyStdAccess(fn: Access.Standard => Access.Standard): ApplicationWithCollaborators = this.access match {
    case std: Access.Standard => withAccess(fn(std))
    case _                    => this
  }
  def withAccess(newAccess: Access): ApplicationWithCollaborators                           = this.focus(_.details.access).replace(newAccess)
  def modifyAccess(fn: Access => Access): ApplicationWithCollaborators                      = this.focus(_.details.access).modify(fn)

  def withToken(newToken: ApplicationToken): ApplicationWithCollaborators                 = this.focus(_.details.token).replace(newToken)
  def modifyToken(fn: ApplicationToken => ApplicationToken): ApplicationWithCollaborators = this.focus(_.details.token).modify(fn)

  def withSubscriptions(subscriptions: Set[ApiIdentifier]): ApplicationWithSubscriptions =
    ApplicationWithSubscriptions(
      this.details,
      this.collaborators,
      subscriptions
    )
}

object ApplicationWithCollaborators {
  import play.api.libs.json._

  implicit val nameOrdering: Ordering[ApplicationWithCollaborators] = Ordering.by[ApplicationWithCollaborators, ApplicationName](_.details.name)

  implicit val format: Format[ApplicationWithCollaborators] = Json.format[ApplicationWithCollaborators]
}
