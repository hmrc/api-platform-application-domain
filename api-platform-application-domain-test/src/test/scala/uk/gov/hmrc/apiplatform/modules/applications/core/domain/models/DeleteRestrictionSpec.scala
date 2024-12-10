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

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actors
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

class DeleteRestrictionSpec extends BaseJsonFormattersSpec {
  import DeleteRestrictionSpec._

  "DeleteRestriction" should {
    "produce json for DoNotDelete" in {
      Json.toJson[DeleteRestriction](exampleDoNotDelete) shouldBe Json.parse(jsonTextDoNotDelete)
    }
    "produce json for NoRestriction" in {
      Json.toJson[DeleteRestriction](DeleteRestriction.NoRestriction) shouldBe Json.parse(jsonTextNoRestriction)
    }

    "read json for DoNotDelete" in {
      testFromJson[DeleteRestriction](jsonTextDoNotDelete)(
        exampleDoNotDelete
      )
    }
    "read json for NoRestriction" in {
      testFromJson[DeleteRestriction](jsonTextNoRestriction)(
        DeleteRestriction.NoRestriction
      )
    }
  }
}

object DeleteRestrictionSpec extends FixedClock {

  val exampleDoNotDelete = DeleteRestriction.DoNotDelete(
    reason = "Reason",
    actor = Actors.GatekeeperUser("gk-user"),
    timestamp = instant
  )

  val jsonTextDoNotDelete =
    s"""{"deleteRestrictionType":"DO_NOT_DELETE","reason":"Reason","actor":{"actorType":"GATEKEEPER","user":"gk-user"},"timestamp":"$nowAsText"}"""

  val jsonTextNoRestriction =
    """{"deleteRestrictionType":"NO_RESTRICTION"}"""

}
