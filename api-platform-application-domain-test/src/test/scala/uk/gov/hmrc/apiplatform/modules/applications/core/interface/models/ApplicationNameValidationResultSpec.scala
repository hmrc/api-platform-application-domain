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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ApplicationNameValidationResultSpec extends BaseJsonFormattersSpec {
  import ApplicationNameValidationResultSpec._

  "ApplicationNameValidationResult" should {
    "convert to json" in {
      Json.toJson[ApplicationNameValidationResult](ApplicationNameValidationResult.Valid) shouldBe Json.parse(validNameJson)
      Json.toJson[ApplicationNameValidationResult](ApplicationNameValidationResult.Invalid) shouldBe Json.parse(invalidNameJson)
      Json.toJson[ApplicationNameValidationResult](ApplicationNameValidationResult.Duplicate) shouldBe Json.parse(duplicateNameJson)
    }

    "read from json" in {
      testFromJson[ApplicationNameValidationResult](validNameJson)(ApplicationNameValidationResult.Valid)
      testFromJson[ApplicationNameValidationResult](invalidNameJson)(ApplicationNameValidationResult.Invalid)
      testFromJson[ApplicationNameValidationResult](duplicateNameJson)(ApplicationNameValidationResult.Duplicate)
    }
  }
}

object ApplicationNameValidationResultSpec {
  val validNameJson     = """{"validationResult": "VALID"}"""
  val invalidNameJson   = """{"validationResult": "INVALID"}"""
  val duplicateNameJson = """{"validationResult": "DUPLICATE"}"""
}
