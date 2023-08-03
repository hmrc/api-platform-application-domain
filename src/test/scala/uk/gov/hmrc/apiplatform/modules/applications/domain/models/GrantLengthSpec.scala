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

import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec

class GrantLengthSpec extends JsonFormattersSpec with TableDrivenPropertyChecks {

  "GrantLength" should {
    "toString should provide some text" in {
      GrantLength.ONE_DAY.toString() shouldBe "1 day"

      val tests = Table(("days"), GrantLength.values.toSeq: _*)
      forAll(tests) { (grantLength) =>
        grantLength.toString().isEmpty() shouldBe false
      }
    }

    "unsafeFrom succeds for valid value" in {
      GrantLength.unsafeApply(30) shouldBe GrantLength.ONE_MONTH
    }

    "unsafeFrom throws when given a bad value" in {
      intercept[Exception] {
        GrantLength.unsafeApply(2)
      }
    }

    "convert to json" in {
      Json.toJson(GrantLength.ONE_DAY) shouldBe JsNumber(1)
      Json.toJson(GrantLength.ONE_MONTH) shouldBe JsNumber(30)
    }

    "read from json" in {
      testFromJson[GrantLength]("""1""")(GrantLength.ONE_DAY)
      testFromJson[GrantLength]("""30""")(GrantLength.ONE_MONTH)
    }

    "not read invalid json but return a JsError instead" in {
      Json.fromJson[GrantLength](JsNumber(2)) match {
        case e: JsError if (JsError.Message.unapply(e) == Some("Invalid grant length 2")) => succeed
        case _                                                                            => fail("Should have failed validation")
      }
    }
  }
}