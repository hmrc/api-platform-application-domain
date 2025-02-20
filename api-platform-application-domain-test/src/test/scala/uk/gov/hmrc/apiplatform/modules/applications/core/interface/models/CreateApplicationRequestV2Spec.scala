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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.utils.CollaboratorsSyntax
import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress.StringSyntax
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiContext, ApiIdentifier, ApiVersionNbr, ApplicationId, Environment}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.SellResellOrDistributeSpec
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationName

class CreateApplicationRequestV2Spec extends BaseJsonFormattersSpec with CollaboratorsSyntax {

  "CreateApplicationRequestV2" should {
    val admin                = "jim@example.com".toLaxEmail.asAdministrator()
    val sandboxApplicationId = ApplicationId.random

    val upliftRequest = UpliftRequest(
      sellResellOrDistribute = SellResellOrDistributeSpec.example,
      subscriptions = Set(ApiIdentifier(ApiContext("context"), ApiVersionNbr("version"))),
      requestedBy = "bob"
    )

    val request =
      CreateApplicationRequestV2.create(
        name = ApplicationName("an application"),
        access = StandardAccessDataToCopy(),
        description = None,
        environment = Environment.PRODUCTION,
        collaborators = Set(admin),
        upliftRequest = upliftRequest,
        requestedBy = "bob",
        sandboxApplicationId = sandboxApplicationId
      )

    val jsonText =
      s"""{"name":"an application","access":{"redirectUris":[],"postLogoutRedirectUris":[],"overrides":[]},"environment":"PRODUCTION","collaborators":[{"userId":"${admin.userId}","emailAddress":"jim@example.com","role":"ADMINISTRATOR"}],"upliftRequest":{"sellResellOrDistribute":"miscblah","subscriptions":[{"context":"context","version":"version"}],"requestedBy":"bob"},"requestedBy":"bob","sandboxApplicationId":"$sandboxApplicationId"}"""

    "write to json" in {
      Json.toJson(request) shouldBe Json.parse(jsonText)
    }

    "reads from json" in {
      Json.parse(jsonText).as[CreateApplicationRequestV2] shouldBe request
    }

    "reads as base from json" in {
      Json.parse(jsonText).as[CreateApplicationRequest] shouldBe request
    }

    "reads and validates bad login redirect list from V2 json" in {
      val redirectUris = Range.inclusive(1, 6).map(i => s""" "https://abc.com/abc$i" """).mkString(",")

      intercept[IllegalArgumentException] {
        val jsonTextOfBadRequest =
          s"""{"name":"an application","access":{"redirectUris":[$redirectUris],"postLogoutRedirectUris":[],"overrides":[]},"environment":"PRODUCTION","collaborators":[{"userId":"${admin.userId}","emailAddress":"jim@example.com","role":"ADMINISTRATOR"}],"upliftRequest":{"sellResellOrDistribute":"miscblah","subscriptions":[{"context":"context","version":"version"}],"requestedBy":"bob"},"requestedBy":"bob","sandboxApplicationId":"$sandboxApplicationId"}"""
        Json.parse(jsonTextOfBadRequest).as[CreateApplicationRequest]
      }
    }
    "reads and validates bad post logout redirect list from V2 json" in {
      val postLogoutUris = Range.inclusive(1, 6).map(i => s""" "https://abc.com/abc$i" """).mkString(",")

      intercept[IllegalArgumentException] {
        val jsonTextOfBadRequest =
          s"""{"name":"an application","access":{"redirectUris":[],"postLogoutRedirectUris":[$postLogoutUris],"overrides":[]},"environment":"PRODUCTION","collaborators":[{"userId":"${admin.userId}","emailAddress":"jim@example.com","role":"ADMINISTRATOR"}],"upliftRequest":{"sellResellOrDistribute":"miscblah","subscriptions":[{"context":"context","version":"version"}],"requestedBy":"bob"},"requestedBy":"bob","sandboxApplicationId":"$sandboxApplicationId"}"""
        Json.parse(jsonTextOfBadRequest).as[CreateApplicationRequest]
      }
    }

  }
}
