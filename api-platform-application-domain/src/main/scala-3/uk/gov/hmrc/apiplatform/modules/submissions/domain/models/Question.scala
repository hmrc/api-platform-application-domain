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

import scala.collection.immutable.{ListMap, ListSet}

import play.api.libs.json.{Format, Json, OFormat, *}
import uk.gov.hmrc.apiplatform.modules.common.domain.services.ListMapJsonFormatters.given

enum Mark {
  case Fail, Warn, Pass
}

object Mark {
  import cats.Monoid

  given Monoid[Mark] = new Monoid[Mark] {
    def empty: Mark = Pass

    def combine(x: Mark, y: Mark): Mark = (x, y) match {
      case (Fail, _)    => Fail
      case (_, Fail)    => Fail
      case (Warn, _)    => Warn
      case (_, Warn)    => Warn
      case (Pass, Pass) => Pass
    }
  }
}

sealed trait Question {
  def id: Question.Id
  def wording: Wording
  def statement: Option[Statement]
  def afterStatement: Option[Statement]

  def absence: Option[(String, Mark)]

  def absenceText: Option[String] = absence.map(_._1)
  def absenceMark: Option[Mark]   = absence.map(_._2)

  final def isOptional: Boolean = absence.isDefined
}

case class PossibleAnswer(value: String) extends AnyVal {
  def htmlValue: String = value.replace(" ", "-").filter(c => c.isLetterOrDigit || c == '-')
}

trait LabelAndHints {
  self: Question =>

  def label: Option[Question.Label]
  def hintText: Option[NonBulletStatementFragment]
}

case class ErrorInfo private (summary: String, message: Option[String])

object ErrorInfo {
  def apply(summary: String): ErrorInfo                  = new ErrorInfo(summary, None)
  def apply(summary: String, message: String): ErrorInfo = if (summary == message) apply(summary) else new ErrorInfo(summary, Some(message))

  given OFormat[ErrorInfo] = Json.format[ErrorInfo]
}

trait ErrorMessaging {
  self: Question =>

  def errorInfo: Option[ErrorInfo]
}

case class Wording(value: String) extends AnyVal

object Wording {
  given Format[Wording] = Json.valueFormat[Wording]
}

object Question {
  case class Id(value: String) extends AnyVal

  object Id {
    def random = Id(java.util.UUID.randomUUID.toString)

    given KeyReads[Question.Id]  = KeyReads(key => JsSuccess(Question.Id(key)))
    given KeyWrites[Question.Id] = KeyWrites(_.value)
    given Format[Id]             = Json.valueFormat[Id]
  }

  case class Label(value: String) extends AnyVal

  object Label {
    given Format[Label] = Json.valueFormat[Label]
  }

  case class TextQuestion(
      id: Question.Id,
      wording: Wording,
      statement: Option[Statement],
      afterStatement: Option[Statement] = None,
      label: Option[Question.Label] = None,
      hintText: Option[NonBulletStatementFragment] = None,
      validation: Option[TextValidation] = None,
      absence: Option[(String, Mark)] = None,
      errorInfo: Option[ErrorInfo] = None
    ) extends Question with LabelAndHints with ErrorMessaging

  case class AcknowledgementOnly(
      id: Question.Id,
      wording: Wording,
      statement: Option[Statement]
    ) extends Question {
    val absence        = None
    val afterStatement = None
  }

  sealed trait ChoiceQuestion extends Question with LabelAndHints with ErrorMessaging {
    def choices: ListSet[PossibleAnswer]
    def marking: ListMap[PossibleAnswer, Mark]
  }

  sealed trait SingleChoiceQuestion extends ChoiceQuestion

  case class MultiChoiceQuestion(
      id: Question.Id,
      wording: Wording,
      statement: Option[Statement],
      afterStatement: Option[Statement] = None,
      label: Option[Question.Label] = None,
      hintText: Option[NonBulletStatementFragment] = None,
      marking: ListMap[PossibleAnswer, Mark],
      absence: Option[(String, Mark)] = None,
      errorInfo: Option[ErrorInfo] = None
    ) extends ChoiceQuestion {
    lazy val choices: ListSet[PossibleAnswer] = ListSet(marking.keys.toList: _*)
  }

  case class ChooseOneOfQuestion(
      id: Question.Id,
      wording: Wording,
      statement: Option[Statement],
      afterStatement: Option[Statement] = None,
      label: Option[Question.Label] = None,
      hintText: Option[NonBulletStatementFragment] = None,
      marking: ListMap[PossibleAnswer, Mark],
      absence: Option[(String, Mark)] = None,
      errorInfo: Option[ErrorInfo] = None
    ) extends SingleChoiceQuestion {
    lazy val choices: ListSet[PossibleAnswer] = ListSet(marking.keys.toList: _*)
  }

  case class YesNoQuestion(
      id: Question.Id,
      wording: Wording,
      statement: Option[Statement],
      afterStatement: Option[Statement] = None,
      label: Option[Question.Label] = None,
      hintText: Option[NonBulletStatementFragment] = None,
      yesMarking: Mark,
      noMarking: Mark,
      absence: Option[(String, Mark)] = None,
      errorInfo: Option[ErrorInfo] = None
    ) extends SingleChoiceQuestion {

    val YES = PossibleAnswer("Yes")
    val NO  = PossibleAnswer("No")

    lazy val marking: ListMap[PossibleAnswer, Mark] = ListMap(YES -> yesMarking, NO -> noMarking)
    lazy val choices                                = ListSet(YES, NO)
  }

  import play.api.libs.json.*
  import uk.gov.hmrc.play.json.Union

  given Format[Wording] = Json.valueFormat[Wording]

  given Writes[Mark] = Writes {
    case Mark.Fail => JsString("fail")
    case Mark.Warn => JsString("warn")
    case Mark.Pass => JsString("pass")
  }

  given Reads[Mark] = Reads {
    case JsString("fail") => JsSuccess(Mark.Fail)
    case JsString("warn") => JsSuccess(Mark.Warn)
    case JsString("pass") => JsSuccess(Mark.Pass)
    case _                => JsError("Failed to parse Mark value")
  }

  given KeyReads[PossibleAnswer]  = KeyReads(key => JsSuccess(PossibleAnswer(key)))
  given KeyWrites[PossibleAnswer] = KeyWrites(_.value)

  given Reads[ListMap[PossibleAnswer, Mark]] = listMapReads[PossibleAnswer, Mark]

  import Statement.*

  given Format[PossibleAnswer]       = Json.valueFormat[PossibleAnswer]
  given OFormat[TextQuestion]        = Json.format[TextQuestion]
  given OFormat[YesNoQuestion]       = Json.format[YesNoQuestion]
  given OFormat[ChooseOneOfQuestion] = Json.format[ChooseOneOfQuestion]
  given OFormat[MultiChoiceQuestion] = Json.format[MultiChoiceQuestion]
  given OFormat[AcknowledgementOnly] = Json.format[AcknowledgementOnly]

  given Format[Question] = Union.from[Question]("questionType")
    .and[MultiChoiceQuestion]("multi")
    .and[YesNoQuestion]("yesNo")
    .and[ChooseOneOfQuestion]("choose")
    .and[TextQuestion]("text")
    .and[AcknowledgementOnly]("acknowledgement")
    .format
}
