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

import play.api.libs.json.*
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class RateLimitTierSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {
  "RateLimitTier" should {
    "toString should provide some text" in {
      RateLimitTier.Bronze.toString() shouldBe "Bronze"

      val values = Table(("tiers"), RateLimitTier.values.toSeq: _*)
      forAll(values) { (rateLimitTier) =>
        rateLimitTier.toString().isEmpty() shouldBe false
      }
    }

    "displayText correctly" in {
      val values = Table(("tiers"), RateLimitTier.values.toSeq: _*)
      forAll(values) { (rateLimitTier) =>
        rateLimitTier.toString() shouldBe rateLimitTier.toString().toLowerCase().capitalize
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
      Json.toJson[RateLimitTier](RateLimitTier.Rhodium) shouldBe JsString("RHODIUM")
      Json.toJson[RateLimitTier](RateLimitTier.Silver) shouldBe JsString("SILVER")
    }

    "read from json" in {
      testFromJson[RateLimitTier]("\"RHODIUM\"")(RateLimitTier.Rhodium)
      testFromJson[RateLimitTier]("\"Bronze\"")(RateLimitTier.Bronze)
    }

    "not read invalid json but return a JsError instead" in {
      Json.fromJson[RateLimitTier](JsString("UNKNOWN")) match {
        case e: JsError if (JsError.Message.unapply(e) == Some("Invalid Rate Limit Tier UNKNOWN")) => succeed
        case _                                                                                     => fail("Should have failed validation")
      }
    }

    "order of values for display should be correct" in {
      RateLimitTier.values.head shouldBe RateLimitTier.Bronze
      RateLimitTier.values.last shouldBe RateLimitTier.Rhodium
    }
  }
}
