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

import cats.data.NonEmptyList

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ValidationGroupSpec extends BaseJsonFormattersSpec with ValidationGroupFixtures {

  private def objectAsJsonString[A](a: A)(implicit t: Writes[A]) = Json.asciiStringify(Json.toJson(a))

  "ValidationGroup" should {

    val dualValidation     = ValidationGroup("error message", NonEmptyList.of(RegexValidationRule("^.{12,50}+$"), UrlValidationRule))
    val dualValidationText = """{"errorMessage":"error message","rules":[{"RegexValidationRule":{"regex":"^.{12,50}+$"}},{"UrlValidationRule":{}}]}"""

    "marshal json" in {
      objectAsJsonString(groupOne) shouldBe groupOneJson
      objectAsJsonString(dualValidation) shouldBe dualValidationText
    }

    "unmarshal text" in {
      Json.parse(groupOneJson).validate[ValidationGroup] match {
        case JsSuccess(r, _) => r shouldBe groupOne
        case JsError(e)      => fail(s"Should have parsed json text but got $e")
      }
      Json.parse(dualValidationText).validate[ValidationGroup] match {
        case JsSuccess(r, _) => r shouldBe dualValidation
        case JsError(e)      => fail(s"Should have parsed json text but got $e")
      }
    }
  }

}
