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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class DevhubAccessRequirementsSpec extends BaseJsonFormattersSpec with DevhubAccessRequirementsFixtures {
  "JsonFormatter" should {
    "Read from json" in {
      testFromJson("""{ "read": "adminOnly", "write" : "adminOnly" }""")(devhubAccessRequirementsAdmin)
    }
    "writes to json" in {
      testToJson(devhubAccessRequirementsAnyone.copy(write = DevhubAccessRequirement.NoOne))("write" -> "noOne")
    }
    "reads from json with defaults" in {
      testFromJson("""{ }""")(DevhubAccessRequirements.Default)
    }
    "writes to json with defaults" in {
      testToJson(devhubAccessRequirementsAnyone)()
    }
  }

  "Apply" should {
    import DevhubAccessRequirement._

    "prevent greater restrictions on read than on write of NoOne" in {
      DevhubAccessRequirements(NoOne, Anyone) shouldBe DevhubAccessRequirements(NoOne, NoOne)
      DevhubAccessRequirements(NoOne, AdminOnly) shouldBe DevhubAccessRequirements(NoOne, NoOne)
      DevhubAccessRequirements(NoOne, NoOne) shouldBe DevhubAccessRequirements(NoOne, NoOne)
    }

    "prevent greater restrictions on read than on write of AdminOnly" in {
      DevhubAccessRequirements(AdminOnly, Anyone) shouldBe DevhubAccessRequirements(AdminOnly, AdminOnly)
      DevhubAccessRequirements(AdminOnly, AdminOnly) shouldBe DevhubAccessRequirements(AdminOnly, AdminOnly)
      DevhubAccessRequirements(AdminOnly, NoOne) shouldBe DevhubAccessRequirements(AdminOnly, NoOne)
    }
    "prevent greater restrictions on read than on write of Anyone" in {
      DevhubAccessRequirements(Anyone, Anyone) shouldBe DevhubAccessRequirements(Anyone, Anyone)
      DevhubAccessRequirements(Anyone, AdminOnly) shouldBe DevhubAccessRequirements(Anyone, AdminOnly)
      DevhubAccessRequirements(Anyone, NoOne) shouldBe DevhubAccessRequirements(Anyone, NoOne)
    }

  }
}
