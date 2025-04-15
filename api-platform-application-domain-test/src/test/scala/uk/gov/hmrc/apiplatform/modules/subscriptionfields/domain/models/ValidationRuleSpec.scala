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

  "JsonFormatter" should {
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
}
