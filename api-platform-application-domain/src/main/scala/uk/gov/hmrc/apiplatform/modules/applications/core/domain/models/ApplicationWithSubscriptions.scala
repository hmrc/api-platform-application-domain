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
import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models._

case class ApplicationWithSubscriptions(
    details: CoreApplication,
    collaborators: Set[Collaborator],
    subscriptions: Set[ApiIdentifier]
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
  def modify(fn: CoreApplication => CoreApplication): ApplicationWithSubscriptions = this.focus(_.details).modify(fn)

  def withState(newState: ApplicationState): ApplicationWithSubscriptions                 = this.focus(_.details.state).replace(newState)
  def modifyState(fn: ApplicationState => ApplicationState): ApplicationWithSubscriptions = this.focus(_.details.state).modify(fn)

  def withAccess(newAccess: Access): ApplicationWithSubscriptions      = this.focus(_.details.access).replace(newAccess)
  def modifyAccess(fn: Access => Access): ApplicationWithSubscriptions = this.focus(_.details.access).modify(fn)

  def withToken(newToken: ApplicationToken): ApplicationWithSubscriptions                 = this.focus(_.details.token).replace(newToken)
  def modifyToken(fn: ApplicationToken => ApplicationToken): ApplicationWithSubscriptions = this.focus(_.details.token).modify(fn)

  def modifyStdAccess(fn: Access.Standard => Access.Standard): ApplicationWithSubscriptions = this.access match {
    case std: Access.Standard => withAccess(fn(std))
    case _                    => this
  }

  def asAppWithCollaborators: ApplicationWithCollaborators =
    ApplicationWithCollaborators(
      this.details,
      this.collaborators
    )

  def withFieldValues(fieldValues: ApiFieldMap[FieldValue]): ApplicationWithSubscriptionFields =
    ApplicationWithSubscriptionFields(
      this.details,
      this.collaborators,
      this.subscriptions,
      fieldValues
    )

}

object ApplicationWithSubscriptions {
  import play.api.libs.json._

  implicit val nameOrdering: Ordering[ApplicationWithSubscriptions] = Ordering.by[ApplicationWithSubscriptions, ApplicationName](_.details.name)

  implicit val format: Format[ApplicationWithSubscriptions] = Json.format[ApplicationWithSubscriptions]
}
