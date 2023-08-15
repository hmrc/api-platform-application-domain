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

package uk.gov.hmrc.apiplatform.modules.applications.domain.models

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json._

import uk.gov.hmrc.apiplatform.modules.applications.domain.models.RateLimitTier._
import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec

class RateLimitTierSpec extends JsonFormattersSpec with TableDrivenPropertyChecks {
  "RateLimitTier" should {
    "toString should provide some text" in {
      RateLimitTier.BRONZE.toString() shouldBe "BRONZE"

      val tests = Table(("tiers"), RateLimitTier.values.toSeq: _*)
      forAll(tests) { (rateLimitTier) =>
        rateLimitTier.toString().isEmpty() shouldBe false
      }
    }

    "convert to json" in {
      Json.toJson[RateLimitTier](RateLimitTier.RHODIUM) shouldBe JsString("RHODIUM")
      Json.toJson[RateLimitTier](RateLimitTier.SILVER) shouldBe JsString("SILVER")
    }

    "read from json" in {
      testFromJson[RateLimitTier]("\"RHODIUM\"")(RateLimitTier.RHODIUM)
      testFromJson[RateLimitTier]("\"Bronze\"")(RateLimitTier.BRONZE)
    }

    "not read invalid json but return a JsError instead" in {
      Json.fromJson[RateLimitTier](JsString("UNKNOWN")) match {
        case e: JsError if (JsError.Message.unapply(e) == Some("Invalid rate Limit tier UNKNOWN")) => succeed
        case _                                                                                     => fail("Should have failed validation")
      }
    }

    "order for display should be correct" in {
      RateLimitTier.orderedForDisplay.head shouldBe RateLimitTier.BRONZE
      RateLimitTier.orderedForDisplay.last shouldBe RateLimitTier.RHODIUM
    }

    "order of values should be correct" in {
      RateLimitTier.values.head shouldBe RateLimitTier.RHODIUM
      RateLimitTier.values.last shouldBe RateLimitTier.BRONZE
    }
  }
}
