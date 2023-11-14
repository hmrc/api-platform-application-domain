/*
 * Copyright 2023 HM Revenue & Customs
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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment
import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress.StringSyntax
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class CreateApplicationRequestV1Spec extends BaseJsonFormattersSpec with CollaboratorsSyntax {

  "CreateApplicationRequestV1" should {
    val admin     = "jim@example.com".toLaxEmail.asAdministrator()
    val developer = "jim@example.com".toLaxEmail.asDeveloper().copy(userId = admin.userId)

    val request =
      CreateApplicationRequestV1.create(
        name = "an application",
        access = Access.Standard(),
        description = None,
        environment = Environment.PRODUCTION,
        collaborators = Set(admin),
        subscriptions = None
      )

    val jsonText =
      s""" {"name":"an application","access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"environment":"PRODUCTION","collaborators":[{"userId":"${admin.userId}","emailAddress":"jim@example.com","role":"ADMINISTRATOR"}]} """

    "write to json" in {
      Json.toJson(request) shouldBe Json.parse(jsonText)
    }

    "reads from json" in {
      Json.parse(jsonText).as[CreateApplicationRequestV1] shouldBe request
    }

    "reads as base from json" in {
      Json.parse(jsonText).as[CreateApplicationRequest] shouldBe request
    }

    "reads and validates from json" in {
      val redirectUris         = Range.inclusive(1, 6).map(i => s""" "https://abc.com/abc$i" """).mkString(",")
      val jsonTextOfBadRequest =
        s""" {"name":"an application","access":{"redirectUris":[$redirectUris],"overrides":[],"accessType":"STANDARD"},"environment":"PRODUCTION","collaborators":[{"userId":"${admin.userId}","emailAddress":"jim@example.com","role":"ADMINISTRATOR"}]} """

      intercept[IllegalArgumentException] {
        Json.parse(jsonTextOfBadRequest).as[CreateApplicationRequest]
      }
    }

    "create does not throw for non standard apps" in {
      // This test is only checking the flow for validate, i.e. that anything other than Standard Access are created without extra validation
      // the name, collaborator values are all valid as well so should not throw an error
      CreateApplicationRequestV1.create(
        name = "name",
        access = Access.Privileged(),
        description = None,
        environment = Environment.PRODUCTION,
        collaborators = Set(admin),
        subscriptions = None
      )
    }

    "create returns error when name is empty (validate)" in {
      val error = intercept[IllegalArgumentException] {
        CreateApplicationRequestV1.create(
          name = "",
          access = Access.Standard(),
          description = None,
          environment = Environment.PRODUCTION,
          collaborators = Set(admin),
          subscriptions = None
        )
      }

      error.getMessage() shouldBe "requirement failed: name is required"
    }

    "create returns error when no admins as collaborators (validate)" in {
      val error = intercept[IllegalArgumentException] {
        CreateApplicationRequestV1.create(
          name = "someName",
          access = Access.Standard(),
          description = None,
          environment = Environment.PRODUCTION,
          collaborators = Set(developer),
          subscriptions = None
        )
      }

      error.getMessage() shouldBe "requirement failed: at least one ADMINISTRATOR collaborator is required"
    }

    "create returns error when collaborators have same email address (validate)" in {
      val error = intercept[IllegalArgumentException] {
        CreateApplicationRequestV1.create(
          name = "someName",
          access = Access.Standard(),
          description = None,
          environment = Environment.PRODUCTION,
          collaborators = Set(admin, "Jim@Example.com".toLaxEmail.asAdministrator(), developer),
          subscriptions = None
        )
      }

      error.getMessage() shouldBe "requirement failed: duplicate email in collaborator"
    }
  }
}
