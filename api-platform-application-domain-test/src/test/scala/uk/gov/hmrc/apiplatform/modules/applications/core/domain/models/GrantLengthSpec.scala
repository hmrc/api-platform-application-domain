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

    "apply returns None when given a bad period" in {
      GrantLength.apply(Period.ofDays(5)) shouldBe None
    }

    "apply returns None when given a bad number" in {
      GrantLength.apply(5) shouldBe None
    }

    "convert to json" in {
      Json.toJson[GrantLength](GrantLength.ONE_DAY) shouldBe Json.toJson(GrantLength.ONE_DAY.period)
      Json.toJson[GrantLength](GrantLength.ONE_MONTH) shouldBe Json.toJson(GrantLength.ONE_MONTH.period)
    }

    "read from json" in {
      testFromJson[GrantLength]("""1""")(GrantLength.ONE_DAY)
      testFromJson[GrantLength]("""30""")(GrantLength.ONE_MONTH)
    }

    "read Int grant length into Period" in {
      val grantLength = GrantLength.ONE_DAY
      Json.fromJson[GrantLength](JsNumber(1)) shouldBe JsSuccess(grantLength)
    }

    "return JsError for invalid json" in {
      Json.fromJson[GrantLength](JsString("Hello")) shouldBe JsError(JsonValidationError("error.invalid.stringPeriod"))
    }

    "return with show" in {
      GrantLength.ONE_MONTH.show() shouldBe "1 month"
    }
  }
}
