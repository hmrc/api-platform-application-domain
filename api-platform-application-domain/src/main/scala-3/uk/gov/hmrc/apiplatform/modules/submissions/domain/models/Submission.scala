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

import java.time.Instant

import cats.data.NonEmptyList

import play.api.libs.json.EnvReads
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import uk.gov.hmrc.apiplatform.modules.common.domain.services.{InstantJsonFormatter, NonEmptyListFormatters}

import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.SubmissionId
import uk.gov.hmrc.apiplatform.modules.submissions.domain.services.MarkAnswer

object Submission extends EnvReads {
  import NonEmptyListFormatters.given

  type AnswersToQuestions = Map[Question.Id, ActualAnswer]

  val create: (
      String,
      SubmissionId,
      ApplicationId,
      Instant,
      NonEmptyList[GroupOfQuestionnaires],
      QuestionIdsOfInterest,
      AskWhen.Context
  ) => Submission = (requestedBy, id, applicationId, timestamp, groups, questionIdsOfInterest, context) => {

    val initialStatus    = Submission.Status.Created(timestamp, requestedBy)
    val initialInstances = NonEmptyList.of(Submission.Instance(0, Map.empty, NonEmptyList.of(initialStatus)))
    Submission(id, applicationId, timestamp, groups, questionIdsOfInterest, initialInstances, context)
  }

  val addInstance: (Submission.AnswersToQuestions, Submission.Status) => Submission => Submission = (answers, status) =>
    s => {
      val newInstance = Submission.Instance(s.latestInstance.index + 1, answers, NonEmptyList.of(status))
      s.copy(instances = newInstance :: s.instances)
    }

  val changeLatestInstance: (Submission.Instance => Submission.Instance) => Submission => Submission = delta =>
    s => {
      s.copy(instances = NonEmptyList(delta(s.instances.head), s.instances.tail))
    }

  val addStatusHistory: (Submission.Status) => Submission => Submission = newStatus =>
    s => {
      require(Submission.Status.isLegalTransition(s.status, newStatus))

      val currentHistory = s.latestInstance.statusHistory

      // Do not ADD if going from answering to answering - instead replace
      if ((s.status.isAnswering && newStatus.isAnswering)) {
        changeLatestInstance(_.copy(statusHistory = NonEmptyList(newStatus, currentHistory.tail)))(s)
      } else {
        changeLatestInstance(_.copy(statusHistory = newStatus :: currentHistory))(s)
      }
    }

  val automaticallyMark: (Instant, String) => Submission => Submission = (timestamp, name) =>
    s => {
      val markedSubmission: MarkedSubmission = MarkedSubmission(s, MarkAnswer.markSubmission(s))

      if (markedSubmission.isPass) {
        Submission.grant(timestamp, name, Some("Automatically passed"), None)(s)
      } else if (markedSubmission.isFail) {
        Submission.fail(timestamp, name)(s)
      } else {
        Submission.warnings(timestamp, name)(s)
      }
    }

  val updateLatestAnswersTo: (Submission.AnswersToQuestions) => Submission => Submission = (newAnswers) => changeLatestInstance(_.copy(answersToQuestions = newAnswers))

  val decline: (Instant, String, String) => Submission => Submission = (timestamp, name, reasons) => {
    val addDeclinedStatus                                   = addStatusHistory(Status.Declined(timestamp, name, reasons))
    val addNewlyAnsweringInstance: Submission => Submission = (s) => addInstance(s.latestInstance.answersToQuestions, Status.Answering(timestamp, true))(s)

    addDeclinedStatus andThen addNewlyAnsweringInstance
  }

  val grant: (Instant, String, Option[String], Option[String]) => Submission => Submission =
    (timestamp, name, comments, escalatedTo) => addStatusHistory(Status.Granted(timestamp, name, comments, escalatedTo))

  val grantWithWarnings: (Instant, String, String, Option[String]) => Submission => Submission = (timestamp, name, warnings, escalatedTo) => {
    addStatusHistory(Status.GrantedWithWarnings(timestamp, name, warnings, escalatedTo))
  }

  val fail: (Instant, String) => Submission => Submission = (timestamp, name) => addStatusHistory(Status.Failed(timestamp, name))

  val warnings: (Instant, String) => Submission => Submission = (timestamp, name) => addStatusHistory(Status.Warnings(timestamp, name))

  val pendingResponsibleIndividual: (Instant, String) => Submission => Submission = (timestamp, name) => addStatusHistory(Status.PendingResponsibleIndividual(timestamp, name))

  val submit: (Instant, String) => Submission => Submission = (timestamp, requestedBy) => addStatusHistory(Status.Submitted(timestamp, requestedBy))

  sealed trait Status {
    def timestamp: Instant

    def isOpenToAnswers = isCreated || isAnswering

    def canBeMarked = isAnsweredCompletely || isSubmitted || isDeclined || isGranted || isGrantedWithWarnings || isFailed || isWarnings || isPendingResponsibleIndividual

    def isAnsweredCompletely = this match {
      case Submission.Status.Answering(_, completed) => completed
      case _                                         => false
    }

    def isReadOnly: Boolean = this match {
      case _: Submission.Status.Submitted                    => true
      case _: Submission.Status.Granted                      => true
      case _: Submission.Status.GrantedWithWarnings          => true
      case _: Submission.Status.Declined                     => true
      case _: Submission.Status.Failed                       => true
      case _: Submission.Status.Warnings                     => true
      case _: Submission.Status.PendingResponsibleIndividual => true
      case _                                                 => false
    }

    def isCreated = this match {
      case _: Submission.Status.Created => true
      case _                            => false
    }

    def isAnswering = this match {
      case _: Submission.Status.Answering => true
      case _                              => false
    }

    def isSubmitted = this match {
      case _: Submission.Status.Submitted => true
      case _                              => false
    }

    def isGrantedWithOrWithoutWarnings = this match {
      case _: Submission.Status.Granted             => true
      case _: Submission.Status.GrantedWithWarnings => true
      case _                                        => false
    }

    def isGranted = this match {
      case _: Submission.Status.Granted => true
      case _                            => false
    }

    def isGrantedWithWarnings = this match {
      case _: Submission.Status.GrantedWithWarnings => true
      case _                                        => false
    }

    def isDeclined = this match {
      case _: Submission.Status.Declined => true
      case _                             => false
    }

    def isFailed = this match {
      case _: Submission.Status.Failed => true
      case _                           => false
    }

    def isWarnings = this match {
      case _: Submission.Status.Warnings => true
      case _                             => false
    }

    def isPendingResponsibleIndividual = this match {
      case _: Submission.Status.PendingResponsibleIndividual => true
      case _                                                 => false
    }
  }

  object Status {

    case class Declined(
        timestamp: Instant,
        name: String,
        reasons: String
      ) extends Status

    case class Granted(
        timestamp: Instant,
        name: String,
        comments: Option[String],
        escalatedTo: Option[String]
      ) extends Status

    case class GrantedWithWarnings(
        timestamp: Instant,
        name: String,
        warnings: String,
        escalatedTo: Option[String]
      ) extends Status

    case class Failed(
        timestamp: Instant,
        name: String
      ) extends Status

    case class Warnings(
        timestamp: Instant,
        name: String
      ) extends Status

    case class PendingResponsibleIndividual(
        timestamp: Instant,
        name: String
      ) extends Status

    case class Submitted(
        timestamp: Instant,
        requestedBy: String
      ) extends Status

    case class Answering(
        timestamp: Instant,
        completed: Boolean
      ) extends Status

    case class Created(
        timestamp: Instant,
        requestedBy: String
      ) extends Status

    def isLegalTransition(from: Submission.Status, to: Submission.Status): Boolean = (from, to) match {
      case (_: Created, _: Answering)                      => true
      case (Answering(_, true), _: Submitted)              => true
      case (_: Answering, _: Answering)                    => true
      case (_: Submitted, _: Declined)                     => true
      case (_: Submitted, _: Granted)                      => true
      case (_: Submitted, _: GrantedWithWarnings)          => true
      case (_: Submitted, _: Failed)                       => true
      case (_: Submitted, _: Warnings)                     => true
      case (_: Submitted, _: PendingResponsibleIndividual) => true
      case (_: PendingResponsibleIndividual, _: Failed)    => true
      case (_: PendingResponsibleIndividual, _: Warnings)  => true
      case (_: PendingResponsibleIndividual, _: Granted)   => true
      case (_: PendingResponsibleIndividual, _: Declined)  => true
      case (_: Failed, _: Granted)                         => true
      case (_: Failed, _: Declined)                        => true
      case (_: Warnings, _: Granted)                       => true
      case (_: Warnings, _: GrantedWithWarnings)           => true
      case (_: GrantedWithWarnings, _: Declined)           => true
      case (_: GrantedWithWarnings, _: Granted)            => true
      case (_: Granted, _: Declined)                       => true
      case _                                               => false
    }
  }

  case class Instance(
      index: Int,
      answersToQuestions: Submission.AnswersToQuestions,
      statusHistory: NonEmptyList[Submission.Status]
    ) {
    lazy val status: Status = statusHistory.head

    lazy val isOpenToAnswers      = status.isOpenToAnswers
    lazy val isAnsweredCompletely = status.isAnsweredCompletely

    lazy val isCreated                      = status.isCreated
    lazy val isAnswering                    = status.isAnswering
    lazy val isFailed                       = status.isFailed
    lazy val isWarnings                     = status.isWarnings
    lazy val isPendingResponsibleIndividual = status.isPendingResponsibleIndividual
    lazy val isGranted                      = status.isGranted
    lazy val isGrantedWithWarnings          = status.isGrantedWithWarnings
    lazy val isDeclined                     = status.isDeclined
    lazy val isSubmitted                    = status.isSubmitted
  }

  import play.api.libs.json.*
  import uk.gov.hmrc.play.json.Union

  given KeyReads[Questionnaire.Id]  = KeyReads(key => JsSuccess(Questionnaire.Id(key)))
  given KeyWrites[Questionnaire.Id] = KeyWrites(_.value)

  given Writes[QuestionnaireState] = Writes {
    case QuestionnaireState.NotStarted    => JsString("NotStarted")
    case QuestionnaireState.InProgress    => JsString("InProgress")
    case QuestionnaireState.NotApplicable => JsString("NotApplicable")
    case QuestionnaireState.Completed     => JsString("Completed")
  }

  given Reads[QuestionnaireState] = Reads {
    case JsString("NotStarted")    => JsSuccess(QuestionnaireState.NotStarted)
    case JsString("InProgress")    => JsSuccess(QuestionnaireState.InProgress)
    case JsString("NotApplicable") => JsSuccess(QuestionnaireState.NotApplicable)
    case JsString("Completed")     => JsSuccess(QuestionnaireState.Completed)
    case _                         => JsError("Failed to parse QuestionnaireState value")
  }

  given OFormat[QuestionnaireProgress] = Json.format[QuestionnaireProgress]

  given OFormat[QuestionIdsOfInterest] = Json.format[QuestionIdsOfInterest]

  given Reads[Instant] = InstantJsonFormatter.lenientInstantReads

  import Submission.Status.*

  given OFormat[Declined]                     = Json.format[Declined]
  given OFormat[Granted]                      = Json.format[Granted]
  given OFormat[GrantedWithWarnings]          = Json.format[GrantedWithWarnings]
  given OFormat[Failed]                       = Json.format[Failed]
  given OFormat[Warnings]                     = Json.format[Warnings]
  given OFormat[PendingResponsibleIndividual] = Json.format[PendingResponsibleIndividual]
  given OFormat[Submitted]                    = Json.format[Submitted]
  given OFormat[Answering]                    = Json.format[Answering]
  given OFormat[Created]                      = Json.format[Created]

  given OFormat[Submission.Status] = Union.from[Submission.Status]("Submission.StatusType")
    .and[Declined]("declined")
    .and[Granted]("granted")
    .and[GrantedWithWarnings]("grantedWithWarnings")
    .and[Failed]("failed")
    .and[Warnings]("warnings")
    .and[PendingResponsibleIndividual]("pendingResponsibleIndividual")
    .and[Submitted]("submitted")
    .and[Answering]("answering")
    .and[Created]("created")
    .format

  import GroupOfQuestionnaires.given
  import Question.given

  given OFormat[Submission.Instance] = Json.format[Submission.Instance]
  given OFormat[Submission]          = Json.format[Submission]
  given OFormat[ExtendedSubmission]  = Json.format[ExtendedSubmission]
  given OFormat[MarkedSubmission]    = Json.format[MarkedSubmission]

}

case class Submission(
    id: SubmissionId,
    applicationId: ApplicationId,
    startedOn: Instant,
    groups: NonEmptyList[GroupOfQuestionnaires],
    questionIdsOfInterest: QuestionIdsOfInterest,
    instances: NonEmptyList[Submission.Instance],
    context: AskWhen.Context
  ) {
  lazy val allQuestionnaires: NonEmptyList[Questionnaire] = groups.flatMap(g => g.links)

  lazy val allQuestions: NonEmptyList[Question] = allQuestionnaires.flatMap(l => l.questions.map(_.question))

  def findQuestion(questionId: Question.Id): Option[Question] = allQuestions.find(q => q.id == questionId)

  def findQuestionnaireContaining(questionId: Question.Id): Option[Questionnaire] =
    allQuestionnaires.find(qn =>
      qn.questions.exists(qi =>
        qi.question.id == questionId
      )
    )

  lazy val latestInstance = instances.head

  lazy val status: Submission.Status = latestInstance.statusHistory.head
}

case class ExtendedSubmission(
    submission: Submission,
    questionnaireProgress: Map[Questionnaire.Id, QuestionnaireProgress]
  )

case class MarkedSubmission(
    submission: Submission,
    markedAnswers: Map[Question.Id, Mark]
  ) {
  lazy val isFail = markedAnswers.values.toList.contains(Mark.Fail) | markedAnswers.values.filter(_ == Mark.Warn).size >= 4
  lazy val isWarn = markedAnswers.values.toList.contains(Mark.Warn)
  lazy val isPass = !isWarn && !isFail
}
