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

package uk.gov.hmrc.apiplatform.modules.submissions.domain.models

import scala.util.{Success, Try}

import org.apache.commons.validator.routines.EmailValidator

import play.api.libs.json.{Json, OFormat}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationName

sealed trait TextValidation {
  def isValid(text: String): Boolean = this.validate(text).isRight

  def validate(text: String): Either[String, String] = this match {
    case TextValidation.Url =>
      Try(new java.net.URL(text)) match {
        case Success(_) => Right(text)
        case _          => Left(s"$text is not a valid Url")
      }

    case TextValidation.Email =>
      if (TextValidation.emailValidator.isValid(text)) {
        Right(text)
      } else {
        Left(s"$text is not a valid email")
      }

    case TextValidation.MatchRegex(regex) => {
      val matcher = regex.r
      text match {
        case matcher(_*) => Right(text)
        case _           => Left(s"$text does not match expected pattern")
      }
    }

    case TextValidation.ApplicationName => {
      if (ApplicationName(text).isValid) {
        Right(text)
      } else {
        Left(s"$text is not a valid application name")
      }

    }
  }
}

object TextValidation {
  val emailValidator = EmailValidator.getInstance()

  case object Url                      extends TextValidation
  case class MatchRegex(regex: String) extends TextValidation
  case object Email                    extends TextValidation
  case object ApplicationName          extends TextValidation

  import uk.gov.hmrc.play.json.Union

  implicit val formatAsUrl: OFormat[Url.type]                         = Json.format[Url.type]
  implicit val formatMatchRegex: OFormat[MatchRegex]                  = Json.format[MatchRegex]
  implicit val formatIsEmail: OFormat[Email.type]                     = Json.format[Email.type]
  implicit val formatIsApplicationName: OFormat[ApplicationName.type] = Json.format[ApplicationName.type]

  implicit val formatTextValidation: OFormat[TextValidation] = Union.from[TextValidation]("validationType")
    .and[Url.type]("url")
    .and[MatchRegex]("regex")
    .and[Email.type]("email")
    .and[ApplicationName.type]("applicationName")
    .format
}
