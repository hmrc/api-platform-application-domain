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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import java.util.UUID

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class ApiFieldMapSpec
    extends HmrcSpec
    with ApiIdentifierFixtures
    with FieldsFixtures
    with ClientIdFixtures {

  "ApiFieldMap" should {
    "provide an default constructor" in {
      val x: ApiFieldMap[Int] = ApiFieldMap.empty[Int]

      x.isEmpty shouldBe true
    }

    "extract an api from an ApiIdentifier" in {
      val fieldIntMap = Map(
        FieldNameData.one -> 1,
        FieldNameData.two -> 2
      )

      val x = Map(apiContextOne -> Map(apiVersionNbrOne -> fieldIntMap))

      ApiFieldMap.extractApi(apiIdentifierOne)(x) shouldBe fieldIntMap
      ApiFieldMap.extractApi(apiIdentifierTwo)(x) shouldBe Map.empty
      ApiFieldMap.extractApi(apiIdentifierThree)(x) shouldBe Map.empty
    }
  }

  "read from BulkSubscriptionFieldsResponse json" in {
    implicit val writessSubscriptionFieldsId: Writes[SubscriptionFieldsId] = Json.valueWrites[SubscriptionFieldsId]
    implicit val writesSubscriptionFields: Writes[SubscriptionFields]      = Json.writes[SubscriptionFields]
    implicit val writesBulk: Writes[BulkSubscriptionFieldsResponse]        = Json.writes[BulkSubscriptionFieldsResponse]

    val fieldsIdOne: SubscriptionFieldsId = SubscriptionFieldsId(UUID.randomUUID())

    val bulk = BulkSubscriptionFieldsResponse(
      List(
        SubscriptionFields(clientIdOne, apiContextOne, apiVersionNbrOne, fieldsIdOne, fieldsMapOne),
        SubscriptionFields(clientIdOne, apiContextOne, apiVersionNbrTwo, fieldsIdOne, fieldsMapTwo),
        SubscriptionFields(clientIdOne, apiContextTwo, apiVersionNbrOne, fieldsIdOne, fieldsMapThree)
      )
    )

    val response = Json.toJson(bulk)

    import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.Implicits.OverrideForBulkResponse._

    Json.fromJson[ApiFieldMap[FieldValue]](response) shouldBe JsSuccess(Map(
      apiContextOne -> Map(
        apiVersionNbrOne -> fieldsMapOne,
        apiVersionNbrTwo -> fieldsMapTwo
      ),
      apiContextTwo -> Map(
        apiVersionNbrOne -> fieldsMapThree
      )
    ))
  }
}
