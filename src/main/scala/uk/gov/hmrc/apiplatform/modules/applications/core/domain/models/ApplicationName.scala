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

import cats.data.Validated._
import cats.data._
import cats.syntax.all._

import play.api.libs.json.{Format, Json}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationName._

case class ApplicationName(value: String) extends AnyVal {
  override def toString(): String = value

  def isValid: Boolean = validate().isValid

  private def validateCharacters(): ValidationResult[String] = Validated.condNec(
    !value.toCharArray.exists(c => c < 32 || c > 126 || disallowedCharacters.contains(c)),
    value,
    ApplicationNameInvalidCharacters
  )

  private def validateLength(): ValidationResult[String] =
    Validated.condNec(
      value.length >= minimumLength && value.length <= maximumLength,
      value,
      ApplicationNameInvalidLength
    )

  def validate(): ValidationResult[ApplicationName] = {
    (validateCharacters(), validateLength()).mapN((_, _) => ApplicationName(value))
  }

}

object ApplicationName {

  type ValidationResult[A] = ValidatedNec[ApplicationNameValidationFailed, A]

  val minimumLength        = 2
  val maximumLength        = 50
  val disallowedCharacters = """<>/\"'`"""

  implicit val format: Format[ApplicationName] = Json.valueFormat[ApplicationName]
}

trait ApplicationNameValidationFailed

case object ApplicationNameInvalidLength     extends ApplicationNameValidationFailed
case object ApplicationNameInvalidCharacters extends ApplicationNameValidationFailed
