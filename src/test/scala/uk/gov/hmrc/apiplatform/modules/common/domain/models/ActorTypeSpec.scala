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

package uk.gov.hmrc.apiplatform.modules.common.domain.models

import org.scalatest.OptionValues
import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec

class ActorTypeSpec extends JsonFormattersSpec with OptionValues {
  "ActorTypes" when {
    "looking up from description" should {
      "collaborator" in {
        ActorTypes.fromDescription("Application Collaborator") shouldBe Some(ActorTypes.COLLABORATOR)
      }

      "gatekeeper" in {
        ActorTypes.fromDescription("Gatekeeper User") shouldBe Some(ActorTypes.GATEKEEPER)
      }

      "scheduled job" in {
        ActorTypes.fromDescription("Scheduled Job") shouldBe Some(ActorTypes.SCHEDULED_JOB)
      }

      "unknown" in {
        ActorTypes.fromDescription("Unknown") shouldBe Some(ActorTypes.UNKNOWN)
      }
    }

    "looking up from String" should {
      "unknown" in {
        ActorTypes.fromString("UNKNOWN") shouldBe Some(ActorTypes.UNKNOWN)
      }
      "scheduled job" in {
        ActorTypes.fromString("SCHEDULED_JOB") shouldBe Some(ActorTypes.SCHEDULED_JOB)
      }
      "gatekeeper" in {
        ActorTypes.fromString("GATEKEEPER") shouldBe Some(ActorTypes.GATEKEEPER)
      }
      "collaborator" in {
        ActorTypes.fromString("COLLABORATOR") shouldBe Some(ActorTypes.COLLABORATOR)
      }
    }

    "reading json" should {
      "read full json" in {
        testFromJson[ActorType]("""{"description" : "Application Collaborator", "type": "COLLABORATOR"}""")(ActorTypes.COLLABORATOR)
      }
      "read just the type" in {
        testFromJson[ActorType](""""GATEKEEPER"""")(ActorTypes.GATEKEEPER)
      }
      "produces json" in {
        testToJson[ActorType](ActorTypes.SCHEDULED_JOB)("description" -> "Scheduled Job", "type" -> "SCHEDULED_JOB")
      }
    }
  }
}
