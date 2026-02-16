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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessLevel._
import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement._

class AccessRequirementsSpec extends BaseJsonFormattersSpec with AccessRequirementsFixtures {
  "JsonFormatter" should {
    "Read" in {
      testFromJson("""{ "devhub" : { "read": "anyone", "write" : "anyone" }}""")(accessRequirementAnyone)
    }

    "write" in {
      Json.toJson(accessRequirementAdmin).toString shouldBe """{"devhub":{"read":"adminOnly","write":"adminOnly"}}"""
    }

    "marshalling a default correctly" in {
      val rq = AccessRequirements.Default

      Json.stringify(Json.toJson(rq)) shouldBe """{"devhub":{}}"""
    }

    "marshalling with some devhub requirements correctly" in {
      // read is set explicity, but write will be given this greater restriction too.
      val rq = AccessRequirements(devhub = DevhubAccessRequirements.apply(read = DevhubAccessRequirement.AdminOnly))

      Json.stringify(Json.toJson(rq)) shouldBe """{"devhub":{"read":"adminOnly","write":"adminOnly"}}"""
    }

    "unmarshall with default correctly" in {
      Json.fromJson[AccessRequirements](Json.parse("""{"devhub":{}}""")) shouldBe JsSuccess(AccessRequirements.Default)
    }

    "unmarshall with non default correctly" in {
      Json.fromJson[AccessRequirements](Json.parse("""{"devhub":{"read":"adminOnly"}}""")) shouldBe JsSuccess(
        AccessRequirements(devhub = DevhubAccessRequirements(read = DevhubAccessRequirement.AdminOnly))
      )
    }
  }

  "AccessRequirements" should {

    "satisfy the Anyone requirement" in {
      Developer.satisfiesRequirement(Anyone) shouldBe true
    }
    "not satisfy the AdminOnly requirement" in {
      Developer.satisfiesRequirement(AdminOnly) shouldBe false
    }
    "not satisfy the NoOne requirement" in {
      Developer.satisfiesRequirement(NoOne) shouldBe false
    }
  }

  "Admin" should {
    "satisfy the Anyone requirement" in {
      Admininstator.satisfiesRequirement(Anyone) shouldBe true
    }
    "satisfy the AdminOnly requirement" in {
      Admininstator.satisfiesRequirement(AdminOnly) shouldBe true
    }
    "not satisfy the NoOne requirement" in {
      Admininstator.satisfiesRequirement(NoOne) shouldBe false
    }
  }
}
