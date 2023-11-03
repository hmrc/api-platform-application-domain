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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actors
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import play.api.libs.json.Json

class StateHistorySpec extends BaseJsonFormattersSpec {
  import StateHistorySpec._

  "StateHistory" should {
    "produce json" in {
      Json.toJson(example) shouldBe Json.parse(jsonText)
    }

    "read json" in {
      testFromJson(jsonText)(example)
    }
  }
}

object StateHistorySpec extends FixedClock {
  val id = ApplicationId.random
  val example  = StateHistory(id, State.PRODUCTION, Actors.Unknown, Some(State.PRE_PRODUCTION), None, now())
  val jsonText = s"""{"applicationId":"$id","state":"PRODUCTION","actor":{"actorType":"UNKNOWN"},"previousState":"PRE_PRODUCTION","changedAt":"2020-01-02T03:04:05.006Z"}"""
}
