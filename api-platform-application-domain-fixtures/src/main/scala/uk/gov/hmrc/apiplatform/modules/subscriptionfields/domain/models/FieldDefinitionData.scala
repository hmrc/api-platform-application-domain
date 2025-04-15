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

object FieldDefinitionData {

  val one = FieldDefinition(
    name = FieldNameData.one,
    description = "blah",
    hint = "a hint",
    `type` = FieldDefinitionType.SECURE_TOKEN,
    shortDescription = "short",
    validation = None,
    access = AccessRequirementsData.anyone
  )
}

trait FieldDefinitionFixtures extends AccessRequirementsFixtures with ValidationGroupFixtures {
  val fieldDefnOne = FieldDefinitionData.one
  val fieldDefnTwo = FieldDefinitionData.one.copy(access = accessRequirementAdmin)
}
