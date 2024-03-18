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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import java.time.Duration
import java.time.temporal.ChronoUnit

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.GrantLength.durationFormat

class DurationSpec extends HmrcSpec with FixedClock {
  val duration = Duration.of(1, ChronoUnit.DAYS)

  "Duration" should {

    "write to json" in {

      Json.toJson[Duration](duration) shouldBe Json.obj(
        "length" -> 86400,
        "unit"   -> s"${ChronoUnit.SECONDS.name()}"
      )
    }

    "read from json" in {

      val jsonText = s""" {"length":1, "unit":"days"} """
      Json.parse(jsonText).as[Duration] shouldBe duration
    }
  }
}
