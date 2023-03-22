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

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import play.api.libs.json.EnvReads
import java.time.LocalDateTime
import java.time.Instant
import play.api.libs.json._
import play.api.libs.json.EnvWrites
import java.time._

class CommonJsonFormattersSpec extends AnyWordSpec with Matchers {

  object TypicalJsonFormatters extends EnvReads with EnvWrites

  val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())

  val localDateTime: LocalDateTime = LocalDateTime.now(clock)
  val instant: Instant = clock.instant()

  "writing from localdatetime" should {
    val jsonText = {
      import TypicalJsonFormatters._
      Json.toJson(localDateTime).toString()
    }

    "be readable to localdatetime" in {
      Json.fromJson[LocalDateTime](Json.parse(jsonText)) shouldBe JsSuccess(localDateTime)
    }

    "NOT be readable to instant" in {
      Json.fromJson[Instant](Json.parse(jsonText)) match {
        case _ : JsError => succeed
        case _ => fail("Should not parse")
      }
    }

    "be readable to instant using tolerant reader" in {
      import CommonJsonFormatters._
      Json.fromJson[Instant](Json.parse(jsonText)) shouldBe JsSuccess(instant)
    }
  }

  "writing from instant" should {
    val jsonText: String =  {
      import TypicalJsonFormatters._
      Json.toJson(instant).toString()
    }

    "be readable to localdatetime" in {
      Json.fromJson[LocalDateTime](Json.parse(jsonText)) shouldBe JsSuccess(localDateTime)
    }

    "be readable to instant" in {
      Json.fromJson[Instant](Json.parse(jsonText)) shouldBe JsSuccess(instant)
    }

    "be readable to instant using tolerant reader" in {
      import CommonJsonFormatters._
      Json.fromJson[Instant](Json.parse(jsonText)) shouldBe JsSuccess(instant)
    }
  }
}