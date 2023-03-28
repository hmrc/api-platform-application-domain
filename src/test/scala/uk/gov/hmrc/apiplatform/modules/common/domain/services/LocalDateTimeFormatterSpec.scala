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

package uk.gov.hmrc.apiplatform.modules.common.domain.services

import java.time.LocalDateTime

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.utils.HmrcSpec

class LocalDateTimeFormatterSpec extends HmrcSpec {
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.LocalDateTimeFormatter._

  "LocalDateTimeFormatter" when {
    val aTimestamp              = LocalDateTime.of(2020, 1, 1, 12, 1, 2)
    val threeMillis             = 3 * 1000 * 1000
    val threeMillisAndFourNanos = 3 * 1000 * 1000 + 3

    "writing json" should {
      "for no millis" in {
        Json.toJson(aTimestamp) shouldBe JsString("2020-01-01T12:01:02Z")
      }

      "for three millis" in {
        Json.toJson(aTimestamp.withNano(threeMillis)) shouldBe JsString("2020-01-01T12:01:02.003Z")
      }

      "for three millis and four nanos" in {
        Json.toJson(aTimestamp.withNano(threeMillisAndFourNanos)) shouldBe JsString("2020-01-01T12:01:02.003Z")
      }
    }

    "reading json" should {
      "read with no millis and no Z" in {
        Json.parse(""" "2020-01-01T12:01:02" """).as[LocalDateTime] shouldBe aTimestamp
      }
      "read with no millis and Z" in {
        Json.parse(""" "2020-01-01T12:01:02Z" """).as[LocalDateTime] shouldBe aTimestamp
      }
      "read with 3 millis" in {
        Json.parse(""" "2020-01-01T12:01:02.003" """).as[LocalDateTime] shouldBe aTimestamp.withNano(threeMillis)
      }
      "read with 3 millis and Z" in {
        Json.parse(""" "2020-01-01T12:01:02.003Z" """).as[LocalDateTime] shouldBe aTimestamp.withNano(threeMillis)
      }
    }
  }
}
