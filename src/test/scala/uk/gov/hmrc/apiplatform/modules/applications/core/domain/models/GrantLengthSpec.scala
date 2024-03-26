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

import java.time.Period

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class GrantLengthSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "GrantLength" should {
    "toString should provide some text" in {
      GrantLength.ONE_DAY.toString() shouldBe "1 day"

      val tests = Table(("days"), GrantLength.values.toSeq: _*)
      forAll(tests) { (grantLength) =>
        grantLength.toString().isEmpty() shouldBe false
      }
    }

    "1-param apply succeeds for valid value" in {
      GrantLength.apply(30) shouldBe Some(GrantLength.ONE_MONTH)
    }

    "apply throws when given a bad value" in {
      intercept[Exception] {
        GrantLength.apply(Period.ofDays(5))
      }
    }

    "convert to json" in {
      Json.toJson[GrantLength](GrantLength.ONE_DAY) shouldBe JsNumber(1)
      Json.toJson[GrantLength](GrantLength.ONE_MONTH) shouldBe JsNumber(30)
    }

    "read from json" in {
      testFromJson[GrantLength]("""1""")(GrantLength.ONE_DAY)
      testFromJson[GrantLength]("""30""")(GrantLength.ONE_MONTH)
    }

    "read Int grant length into Period" in {
      val grantLength = GrantLength.ONE_DAY
      Json.fromJson[GrantLength](JsNumber(1)) shouldBe JsSuccess(grantLength)
    }

    "not read invalid json but throw an IllegalStateException instead" in {
      val e = intercept[IllegalStateException] {
        Json.fromJson[GrantLength](JsNumber(2))
      }
      e.getMessage shouldBe "P2D is not an expected value. It should only be one of ('0 days, 1 day', '1 month', '3 months', '6 months', '1 year', '18 months', '3 years', '5 years', '10 years', '100 years')"
    }
  }
}
