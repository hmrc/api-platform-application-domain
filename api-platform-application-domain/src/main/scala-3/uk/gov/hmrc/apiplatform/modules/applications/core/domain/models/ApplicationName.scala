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

import cats.data.*
import cats.data.Validated.*
import cats.syntax.all.*

import play.api.libs.json.*

opaque type ValidatedApplicationName <: String = String

object ValidatedApplicationName {

  extension (s: ValidatedApplicationName) {
    def value: String = s
  }

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

  def apply(raw: String): Option[ValidatedApplicationName] =
    validate(raw) match {
      case Valid(applicationName) => Some(applicationName)
      case _                      => None
    }

  def unsafeApply(raw: String): ValidatedApplicationName =
    validate(raw).getOrElse(throw new RuntimeException(s"$raw is not a valid ApplicationName"))

  def validate(applicationName: String): ValidationResult[ValidatedApplicationName] = {
    (validateCharacters(applicationName), validateLength(applicationName)).mapN((_, _) => new ValidatedApplicationName(applicationName))
  }

  given Format[ValidatedApplicationName] = Format(Reads.StringReads, Writes.StringWrites)
}

opaque type ApplicationName <: String = String

object ApplicationName {
  def apply(value: String): ApplicationName = new ApplicationName(value.trim())

  given Format[ApplicationName] = Format(Reads.StringReads, Writes.StringWrites)
}

trait ApplicationNameValidationFailed

case object ApplicationNameInvalidLength     extends ApplicationNameValidationFailed
case object ApplicationNameInvalidCharacters extends ApplicationNameValidationFailed
