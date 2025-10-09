/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApiIdentifier
import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.ApiFieldMap
import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.FieldValue
import play.api.libs.json._
import cats.Apply

final case class QueriedApplication(
  details: CoreApplication,
  collaborators: Set[Collaborator],
  subscriptions: Option[Set[ApiIdentifier]],
  fieldValues: Option[ApiFieldMap[FieldValue]],
  stateHistory: Option[List[StateHistory]]
) {

  def asAppWithCollaborators = ApplicationWithCollaborators(details, collaborators)

  def asAppWithSubscriptions = subscriptions.map(s => ApplicationWithSubscriptions(details, collaborators, s))
  def unsafeAsAppWithSubscriptions = ApplicationWithSubscriptions(details, collaborators, subscriptions.get)

  def asAppSubsFields = Apply[Option].map2(subscriptions, fieldValues) {
    case (s,f) => ApplicationWithSubscriptionFields(details, collaborators, s, f)
  }
  def unsafeAsAppWithSubFields = ApplicationWithSubscriptionFields(details, collaborators, subscriptions.get, fieldValues.get)
}

object QueriedApplication {
  implicit val format: OFormat[QueriedApplication] = Json.format[QueriedApplication]

  def apply(app: ApplicationWithCollaborators): QueriedApplication = QueriedApplication(app.details, app.collaborators, None, None, None)
  def apply(app: ApplicationWithSubscriptions): QueriedApplication = QueriedApplication(app.details, app.collaborators, Some(app.subscriptions), None, None)
  def apply(app: ApplicationWithSubscriptionFields): QueriedApplication = QueriedApplication(app.details, app.collaborators, Some(app.subscriptions), Some(app.fieldValues), None)
}
