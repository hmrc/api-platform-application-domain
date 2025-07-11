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

object ValidationGroupData {
  val one = ValidationGroup("Bang", NonEmptyList.one(ValidationRuleData.one))
  val two = ValidationGroup("Boom", NonEmptyList.one(ValidationRuleData.two))
}

trait ValidationGroupFixtures extends ValidationRuleFixtures {
  val groupOne = ValidationGroupData.one
  val groupTwo = ValidationGroupData.two

  val groupOneJson = """{"errorMessage":"Bang","rules":[{"RegexValidationRule":{"regex":".*"}}]}"""
  val groupTwoJson = """{"errorMessage":"Bang","rules":[{"UrlValidationRule"]}"""

}
