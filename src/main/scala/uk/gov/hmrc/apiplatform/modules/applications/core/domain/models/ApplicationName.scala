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

trait ApplicationName {
  def value: String
  def isValid: Boolean
}
case class ValidatedApplicationName private (value: String) extends ApplicationName {
  val isValid = true
}

object ValidatedApplicationName {
  type ValidationResult[A] = ValidatedNec[ApplicationNameValidationFailed, A]

  private val minimumLength        = 2
  private val maximumLength        = 50
  private val disallowedCharacters = """<>/\"'`"""

  private def validateCharacters(applicationName: String): ValidationResult[String] = Validated.condNec(
      !applicationName.toCharArray.exists(c => c < 32 || c > 126 || disallowedCharacters.contains(c)),
      applicationName,
      ApplicationNameInvalidCharacters
    )

  private def validateLength(applicationName: String): ValidationResult[String] =
      Validated.condNec(
        applicationName.length >= minimumLength && applicationName.length <= maximumLength,
        applicationName,
        ApplicationNameInvalidLength
      )

  def validate(applicationName: String): ValidationResult[ValidatedApplicationName] = {
    (validateCharacters(applicationName), validateLength(applicationName)).mapN((_, _) => new ValidatedApplicationName(applicationName))
  }
}

case class InvalidApplicationName(value: String) extends ApplicationName {
  val isValid = false
}


object ApplicationName {
  def apply(raw: String): ApplicationName = {
    ValidatedApplicationName.validate(raw).getOrElse(InvalidApplicationName(raw))
  }
}

///////////////////

// case class ApplicationName(value: String) extends AnyVal {
//   override def toString(): String = value

//   def isValid: Boolean = validate().isValid

//   def validate(): ValidationResult[ApplicationName] = ApplicationName.validate(value)

// }

// object ApplicationName {

//   type ValidationResult[A] = ValidatedNec[ApplicationNameValidationFailed, A]

//   val minimumLength        = 2
//   val maximumLength        = 50
//   val disallowedCharacters = """<>/\"'`"""

//   implicit val format: Format[ApplicationName] = Json.valueFormat[ApplicationName]

//   def validate(applicationName: String): ValidationResult[ApplicationName] = {
//     (validateCharacters(applicationName), validateLength(applicationName)).mapN((_, _) => ApplicationName(applicationName))
//   }

//   private def validateCharacters(applicationName: String): ValidationResult[String] = Validated.condNec(
//     !applicationName.toCharArray.exists(c => c < 32 || c > 126 || disallowedCharacters.contains(c)),
//     applicationName,
//     ApplicationNameInvalidCharacters
//   )

//   private def validateLength(applicationName: String): ValidationResult[String] =
//     Validated.condNec(
//       applicationName.length >= minimumLength && applicationName.length <= maximumLength,
//       applicationName,
//       ApplicationNameInvalidLength
//     )
// }

trait ApplicationNameValidationFailed

case object ApplicationNameInvalidLength     extends ApplicationNameValidationFailed
case object ApplicationNameInvalidCharacters extends ApplicationNameValidationFailed
