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

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDateTime, ZoneOffset}

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.utils.HmrcSpec

class InstantFormatterSpec extends HmrcSpec {
  val sixMillisInNanos             = 6 * 1000 * 1000
  val aLocalDateTime               = LocalDateTime.of(2020, 1, 2, 3, 4, 5, sixMillisInNanos)
  val anInstant: Instant           = aLocalDateTime.toInstant(ZoneOffset.UTC)
  val anInstantJustMillis: Instant = anInstant.truncatedTo(ChronoUnit.MILLIS)

  val instantText = "2020-01-02T03:04:05.006"

  "InstantFormatter" when {
    "Using no time zones" should {
      import InstantFormatter.NoTimeZone._

      "read from json" in {
        Json.parse(""" "2020-01-02T03:04:05.006" """).as[Instant] shouldBe anInstantJustMillis
        Json.parse(""" "2020-01-02T03:04:05.006Z" """).as[Instant] shouldBe anInstantJustMillis
      }

      "read from json even with timezones" in {
        Json.parse(""" "2020-01-02T03:04:05.006+0000" """).as[Instant] shouldBe anInstantJustMillis
        Json.parse(""" "2020-01-02T04:04:05.006+0100" """).as[Instant] shouldBe anInstantJustMillis
      }

      "write to json" in {
        Json.toJson[Instant](anInstantJustMillis) shouldBe JsString(instantText)
        Json.toJson[Instant](anInstant) shouldBe JsString(instantText)
      }

      "write such that it can be read by LDT formatter (also dropping nanos)" in {
        import LocalDateTimeFormatter._

        val jsValue = Json.toJson[Instant](anInstant)
        jsValue.as[LocalDateTime].toInstant(ZoneOffset.UTC) shouldBe anInstantJustMillis
      }

      "read from an LDT formatted string" in {
        val jsValue = Json.toJson[LocalDateTime](aLocalDateTime)
        jsValue.as[Instant] shouldBe anInstantJustMillis
      }
    }

    "Using time zones" should {
      import InstantFormatter.WithTimeZone._
      val instantTextWithTZ = "2020-01-02T03:04:05.006+0000"

      "read from json" in {
        Json.parse(""" "2020-01-02T03:04:05.006" """).as[Instant] shouldBe anInstantJustMillis
        Json.parse(""" "2020-01-02T03:04:05.006Z" """).as[Instant] shouldBe anInstantJustMillis
      }

      "read from json with timezones" in {
        Json.parse(""" "2020-01-02T03:04:05.006+0000" """).as[Instant] shouldBe anInstantJustMillis
        Json.parse(""" "2020-01-02T04:04:05.006+0100" """).as[Instant] shouldBe anInstantJustMillis
      }

      "write to json" in {
        Json.toJson[Instant](anInstantJustMillis) shouldBe JsString(instantTextWithTZ)
        Json.toJson[Instant](anInstant) shouldBe JsString(instantTextWithTZ)
      }

      "read from an LDT formatted string" in {
        val jsValue = Json.toJson[LocalDateTime](aLocalDateTime)
        jsValue.as[Instant] shouldBe anInstantJustMillis
      }
    }
  }
}
