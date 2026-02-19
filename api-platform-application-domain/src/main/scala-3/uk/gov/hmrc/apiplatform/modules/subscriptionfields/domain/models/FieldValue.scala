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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import scala.util.Random

opaque type FieldValue <: String = String

object FieldValue {

  extension (fv: FieldValue) {
    def validateAgainstRule(rule: ValidationRule): Boolean = rule.validateAgainstRule(fv)
  }

  def apply(raw: String): FieldValue = if (raw.isEmpty()) empty else raw

  import play.api.libs.json.*
  given Format[FieldValue] = Format(Reads.StringReads, Writes.StringWrites)

  def empty: FieldValue = ""

  def random: FieldValue = Random.alphanumeric.take(8).mkString // scalastyle:ignore
}
