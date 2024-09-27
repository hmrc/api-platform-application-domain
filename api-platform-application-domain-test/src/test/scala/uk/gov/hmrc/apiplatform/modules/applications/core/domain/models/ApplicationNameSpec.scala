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

import cats.data.Validated.{Invalid, Valid}

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ApplicationNameSpec extends BaseJsonFormattersSpec {

  "ApplicationName" should {
    "toString" in {
      ApplicationName("My App").toString() shouldBe "My App"
    }

    "convert to json" in {
      Json.toJson[ApplicationName](ApplicationName("My App")) shouldBe JsString("My App")
    }

    "read from json" in {
      testFromJson[ApplicationName](JsString("My App").toString)(ApplicationName("My App"))
    }
  }

  "ValidatedApplicationName" should {
    "convert to json" in {
      Json.toJson[ValidatedApplicationName](ValidatedApplicationName.unsafeApply("My App")) shouldBe JsString("My App")
    }

    "read from json" in {
      testFromJson[ValidatedApplicationName](JsString("My App").toString)(ValidatedApplicationName.unsafeApply("My App"))
    }

    "validate a good application name" in {
      ValidatedApplicationName.validate("My app") shouldBe Valid(ValidatedApplicationName.unsafeApply("My app"))
    }

    "check validity of a good application name" in {
      ValidatedApplicationName.validate("My app-restricted, special app worth $100 in BRASS").isValid shouldBe true
      ValidatedApplicationName("My app-restricted, special app worth $100 in BRASS").isDefined shouldBe true
    }

    "check unsafeApply with a good application name " in {
      ValidatedApplicationName.unsafeApply("My app-restricted, special app worth $100 in BRASS") shouldBe ValidatedApplicationName.unsafeApply(
        "My app-restricted, special app worth $100 in BRASS"
      )
    }

    "check unsafeApply with a bad application name " in {
      val ex = intercept[RuntimeException] {
        ValidatedApplicationName.unsafeApply("M")
      }
      ex.getMessage shouldBe "M is not a valid ApplicationName"
    }

    "invalidate an application name with too few characters" in {
      ValidatedApplicationName("M").isDefined shouldBe false
      ValidatedApplicationName.validate("M") match {
        case Invalid(x) =>
          x.length shouldBe 1
          x.head shouldBe ApplicationNameInvalidLength
        case _          => fail("should be invalid")
      }
    }

    "invalidate an application name with too many characters" in {
      ValidatedApplicationName("My app-restricted, special app worth $100 in SILVER").isDefined shouldBe false
      ValidatedApplicationName.validate("My app-restricted, special app worth $100 in SILVER") match {
        case Invalid(x) =>
          x.length shouldBe 1
          x.head shouldBe ApplicationNameInvalidLength
        case _          => fail("should be invalid")
      }
    }

    "invalidate application names with non-ASCII characters" in {
      ValidatedApplicationName("£50").isDefined shouldBe false
      ValidatedApplicationName.validate("£50") match {
        case Invalid(x) =>
          x.length shouldBe 1
          x.head shouldBe ApplicationNameInvalidCharacters
        case _          => fail("should be invalid")
      }
    }

    "invalidate application names with all validations" in {
      ValidatedApplicationName("ɐ").isDefined shouldBe false
      ValidatedApplicationName.validate("ɐ") match {
        case Invalid(x) =>
          x.length shouldBe 2
          x.head shouldBe ApplicationNameInvalidCharacters
          x.toNonEmptyList.tail.head shouldBe ApplicationNameInvalidLength
        case _          => fail("should be invalid")
      }
    }

    "invalidate application names with disallowed special characters" in {
      List('<', '>', '/', '\\', '"', '\'', '`').foreach(c => {
        ValidatedApplicationName.validate(s"invalid $c") match {
          case Invalid(x) =>
            x.length shouldBe 1
            x.head shouldBe ApplicationNameInvalidCharacters
          case _          => fail("should be invalid")
        }
      })
    }
  }
}
