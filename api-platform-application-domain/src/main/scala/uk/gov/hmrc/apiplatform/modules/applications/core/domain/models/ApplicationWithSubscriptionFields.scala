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
import uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models._

case class ApplicationWithSubscriptionFields(
    details: CoreApplication,
    collaborators: Set[Collaborator],
    subscriptions: Set[ApiIdentifier],
    fieldValues: ApiFieldMap[FieldValue]
  ) extends HasEnvironment with HasState with HasAccess with HasCollaborators {
  lazy val id       = details.id
  lazy val name     = details.name
  lazy val clientId = details.clientId

  lazy val deployedTo = details.deployedTo
  lazy val state      = details.state
  lazy val access     = details.access

  // Assist with nesting
  import monocle.syntax.all._
  def modify(fn: CoreApplication => CoreApplication)                                           = this.focus(_.details).modify(fn)
  def withState(newState: ApplicationState): ApplicationWithSubscriptionFields                 = this.focus(_.details.state).replace(newState)
  def modifyState(fn: ApplicationState => ApplicationState): ApplicationWithSubscriptionFields = this.focus(_.details.state).modify(fn)
  def withAccess(newAccess: Access): ApplicationWithSubscriptionFields                         = this.focus(_.details.access).replace(newAccess)
  def modifyAccess(fn: Access => Access): ApplicationWithSubscriptionFields                    = this.focus(_.details.access).modify(fn)

  def modifyStdAccess(fn: Access.Standard => Access.Standard): ApplicationWithSubscriptionFields = this.access match {
    case std: Access.Standard => withAccess(fn(std))
    case _                    => this
  }

  def asAppWithCollaborators: ApplicationWithCollaborators =
    ApplicationWithCollaborators(
      this.details,
      this.collaborators
    )
}

object ApplicationWithSubscriptionFields {
  import play.api.libs.json._

  private val transformOldResponse: OldApplicationWithSubsFields => ApplicationWithSubscriptionFields = (old) => {
    ApplicationWithSubscriptionFields(
      details = old.application.details,
      collaborators = old.application.collaborators,
      subscriptions = old.subscriptions,
      fieldValues = old.subscriptionFieldValues
    )
  }

  val reads: Reads[ApplicationWithSubscriptionFields]            = Json.reads[ApplicationWithSubscriptionFields].orElse(Json.reads[OldApplicationWithSubsFields].map(transformOldResponse))
  val writes: Writes[ApplicationWithSubscriptionFields]          = Json.writes[ApplicationWithSubscriptionFields]
  implicit val format: Format[ApplicationWithSubscriptionFields] = Format(reads, writes)
}
