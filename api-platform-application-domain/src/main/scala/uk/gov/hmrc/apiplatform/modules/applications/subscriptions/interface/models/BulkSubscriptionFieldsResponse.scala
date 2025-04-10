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

package uk.gov.hmrc.apiplatform.modules.applications.subscriptions.interface.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApiVersionNbr

import uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models._

case class BulkSubscriptionFieldsResponse(subscriptions: Seq[SubscriptionFields])

object BulkSubscriptionFieldsResponse {
  import play.api.libs.json._

  private implicit val formatSubscriptionFieldsId: Format[SubscriptionFieldsId] = Json.valueFormat[SubscriptionFieldsId]
  private implicit val formatSubscriptionFields: OFormat[SubscriptionFields]    = Json.format[SubscriptionFields]
  implicit val format: OFormat[BulkSubscriptionFieldsResponse]                  = Json.format[BulkSubscriptionFieldsResponse]

  def toApiFieldMap(response: BulkSubscriptionFieldsResponse): ApiFieldMap[FieldValue] = {
    import cats._
    import cats.implicits._
    type MapType = Map[ApiVersionNbr, Map[FieldName, FieldValue]]

    // Shortcut combining as we know there will never be records for the same version for the same context
    implicit def monoidVersions: Monoid[MapType] =
      new Monoid[MapType] {
        override def combine(x: MapType, y: MapType): MapType = x ++ y
        override def empty: MapType                           = Map.empty
      }

    Monoid.combineAll(
      response.subscriptions.map(s => Map(s.apiContext -> Map(s.apiVersion -> s.fields)))
    )
  }

}
