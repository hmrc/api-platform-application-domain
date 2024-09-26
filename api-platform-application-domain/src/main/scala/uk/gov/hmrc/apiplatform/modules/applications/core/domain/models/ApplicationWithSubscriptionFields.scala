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

case class ApplicationWithSubscriptionFields(
    coreApp: CoreApplication,
    collaborators: Set[Collaborator],
    subscriptions: Set[ApiIdentifier],
    fieldValues: ApiFieldMap[FieldValue]
  ) extends HasEnvironment with HasState with HasAccess with HasCollaborators {
  val id = coreApp.id

  private[core] lazy val deployedTo = coreApp.deployedTo
  lazy val state                    = coreApp.state
  lazy val access                   = coreApp.access
}

object ApplicationWithSubscriptionFields {
  import play.api.libs.json._

  implicit val format: Format[ApplicationWithSubscriptionFields] = Json.format[ApplicationWithSubscriptionFields]
}
