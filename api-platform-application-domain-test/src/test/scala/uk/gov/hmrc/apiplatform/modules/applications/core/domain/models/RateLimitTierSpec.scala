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

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class RateLimitTierSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {
  "RateLimitTier" should {
    "toString should provide some text" in {
      RateLimitTier.BRONZE.toString() shouldBe "BRONZE"

      val values = Table(("tiers"), RateLimitTier.values.toSeq: _*)
      forAll(values) { (rateLimitTier) =>
        rateLimitTier.toString().isEmpty() shouldBe false
      }
    }

    "displayText correctly" in {
      val values = Table(("tiers"), RateLimitTier.values.toSeq: _*)
      forAll(values) { (rateLimitTier) =>
        rateLimitTier.displayText shouldBe rateLimitTier.toString().toLowerCase().capitalize
      }
    }

    "convert lower case string to case object" in {
      val values = Table(("tier", "text"), (RateLimitTier.values.toSeq.map(r => (r, r.toString().toLowerCase()))): _*)
      forAll(values) { (s, t) =>
        RateLimitTier.apply(t) shouldBe Some(s)
        RateLimitTier.unsafeApply(t) shouldBe s
      }
    }

    "convert string value to None when undefined or empty" in {
      RateLimitTier.apply("rubbish") shouldBe None
      RateLimitTier.apply("") shouldBe None
    }

    "throw when string value is invalid" in {
      intercept[RuntimeException] {
        RateLimitTier.unsafeApply("rubbish")
      }.getMessage() should include("Rate Limit Tier")
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
        case e: JsError if (JsError.Message.unapply(e) == Some("Invalid Rate Limit Tier UNKNOWN")) => succeed
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
