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

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyChain, ValidatedNec}

class ApplicationNameSpec extends BaseJsonFormattersSpec {

  "ApplicationName" should {
    "convert to json" in {
      Json.toJson[ApplicationName](ApplicationName("My App")) shouldBe JsString("My App")
    }

    "read from json" in {
      testFromJson[ApplicationName](JsString("My App").toString)(ApplicationName("My App"))
    }

    "validate a good application name" in {
      ApplicationName("My app").validate() shouldBe Valid(ApplicationName("My app"))
    }

    "check validity of a good application name" in {
      ApplicationName("My app-restricted, special app worth $100 in BRASS").isValid shouldBe true
    }

    "invalidate an application name with too few characters" in {
      ApplicationName("M").validate() match {
        case Invalid(x: NonEmptyChain[ApplicationNameValidationFailed]) =>
          x.length shouldBe 1
          x.head shouldBe ApplicationNameInvalidLength(2, 50)
        case _ => fail("should be invalid")
      }
    }

    "invalidate an application name with too many characters" in {
      ApplicationName("My app-restricted, special app worth $100 in SILVER").validate() match {
        case Invalid(x) =>
          x.length shouldBe 1
          x.head shouldBe ApplicationNameInvalidLength(2, 50)
        case _ => fail("should be invalid")
      }
    }

    "invalidate application names with non-ASCII characters" in {

      ApplicationName("ɐ").validate() match {
        case Invalid(x) =>
          x.length shouldBe 2
          x.head shouldBe ApplicationNameInvalidCharacters("""<>/\"'`""")
          x.toNonEmptyList.tail.head shouldBe ApplicationNameInvalidLength(2, 50)
        case _ => fail("should be invalid")
      }
    }

    "invalidate application names with disallowed special characters" in {
      List('<', '>', '/', '\\', '"', '\'', '`').foreach(c => {
        ApplicationName(s"invalid $c").validate() match {
          case Invalid(x) =>
            x.length shouldBe 1
            x.head shouldBe ApplicationNameInvalidCharacters("""<>/\"'`""")
          case _ => fail("should be invalid")
        }
      })
    }
  }
}
