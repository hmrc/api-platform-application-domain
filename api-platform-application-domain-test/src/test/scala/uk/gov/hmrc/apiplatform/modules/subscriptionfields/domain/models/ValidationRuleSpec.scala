/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ValidationRuleSpec extends BaseJsonFormattersSpec with ValidationRuleFixtures {

  val regexJsonText = """{"RegexValidationRule":{"regex":".*"}}"""
  val urlJsonText   = """{"UrlValidationRule":{}}"""

  "ValidationRule" should {
    "Read raw json" in {
      testFromJson[ValidationRule](regexJsonText)(ruleOne)
      testFromJson[ValidationRule](urlJsonText)(ruleTwo)
    }

    "Read bad raw json" in {
      testFailJson[ValidationRule](""" "" """)
    }

    "Read bad json" in {
      testFailJson[ValidationRule]("""{"eh":"bang"}""")
    }

    "Write raw json" in {
      Json.toJson[ValidationRule](ruleOne).toString shouldBe regexJsonText
      Json.toJson[ValidationRule](ruleTwo).toString shouldBe urlJsonText
    }
  }

  val lowerCaseValue = FieldValue("bob")
  val mixedCaseValue = FieldValue("Bob")

  val lowerCaseRule: ValidationRule = RegexValidationRule("""^[a-z]+$""")
  val mixedCaseRule: ValidationRule = RegexValidationRule("""^[a-zA-Z]+$""")

  val atLeastThreeLongRule: ValidationRule = RegexValidationRule("""^.{3}.*$""")
  val atLeastTenLongRule: ValidationRule   = RegexValidationRule("""^.{10}.*$""")

  val validUrl      = FieldValue("https://www.example.com/here/and/there")
  val localValidUrl = FieldValue("https://localhost:9000/")
  val invalidUrls   = List("www.example.com", "ftp://example.com/abc", "https://www example.com", "https://www&example.com", "https://www,example.com").map(FieldValue(_))

  "RegexValidationRule" should {
    "return true when the value is valid - correct case" in {
      lowerCaseRule.validate(lowerCaseValue) shouldBe true
    }
    "return true when the value is valid - long enough" in {
      atLeastThreeLongRule.validate(lowerCaseValue) shouldBe true
    }
    "return false when the value is invalid - wrong case" in {
      lowerCaseRule.validate(mixedCaseValue) shouldBe false
    }
    "return false when the value is invalid - too short" in {
      atLeastTenLongRule.validate(mixedCaseValue) shouldBe false
    }
    "return true when the value is blank" in {
      atLeastTenLongRule.validate(FieldValue("")) shouldBe true
    }
  }

  "url validation rule" should {
    "pass for a matching value" in {
      UrlValidationRule.validate(validUrl) shouldBe true
    }

    "pass for localhost" in {
      UrlValidationRule.validate(localValidUrl) shouldBe true
    }

    "return true when the value is blank" in {
      UrlValidationRule.validate(FieldValue("")) shouldBe true
    }

    "invalid urls" in {
      invalidUrls.map(invalidUrl => {
        UrlValidationRule.validate(invalidUrl) shouldBe false
      })
    }

    "handles internal mdtp domains in url" in {
      UrlValidationRule.validate(FieldValue("https://who-cares.mdtp/pathy/mcpathface")) shouldBe true
    }
  }

}
