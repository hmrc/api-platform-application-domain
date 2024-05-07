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

sealed trait QuestionnaireState

object QuestionnaireState {
  case object NotStarted    extends QuestionnaireState
  case object InProgress    extends QuestionnaireState
  case object NotApplicable extends QuestionnaireState
  case object Completed     extends QuestionnaireState

  def describe(state: QuestionnaireState): String = state match {
    case NotStarted    => "Not Started"
    case InProgress    => "In Progress"
    case NotApplicable => "Not Applicable"
    case Completed     => "Completed"
  }

  def isCompleted(state: QuestionnaireState): Boolean = state match {
    case NotStarted | InProgress => false
    case _                       => true
  }
}

case class QuestionnaireProgress(state: QuestionnaireState, questionsToAsk: List[Question.Id])

case class QuestionIdsOfInterest(
    applicationNameId: Question.Id,
    privacyPolicyId: Question.Id,
    privacyPolicyUrlId: Question.Id,
    termsAndConditionsId: Question.Id,
    termsAndConditionsUrlId: Question.Id,
    organisationUrlId: Question.Id,
    responsibleIndividualIsRequesterId: Question.Id,
    responsibleIndividualNameId: Question.Id,
    responsibleIndividualEmailId: Question.Id,
    identifyYourOrganisationId: Question.Id,
    serverLocationsId: Question.Id
  )

object Submission extends EnvReads with NonEmptyListFormatters {
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
      case (c: Created, a: Answering)                      => true
      case (Answering(_, true), s: Submitted)              => true
      case (a: Answering, b: Answering)                    => true
      case (s: Submitted, d: Declined)                     => true
      case (s: Submitted, g: Granted)                      => true
      case (s: Submitted, w: GrantedWithWarnings)          => true
      case (s: Submitted, f: Failed)                       => true
      case (s: Submitted, w: Warnings)                     => true
      case (s: Submitted, p: PendingResponsibleIndividual) => true
      case (p: PendingResponsibleIndividual, f: Failed)    => true
      case (p: PendingResponsibleIndividual, w: Warnings)  => true
      case (p: PendingResponsibleIndividual, g: Granted)   => true
      case (p: PendingResponsibleIndividual, d: Declined)  => true
      case (f: Failed, g: Granted)                         => true
      case (f: Failed, d: Declined)                        => true
      case (w: Warnings, g: Granted)                       => true
      case (w: Warnings, g: GrantedWithWarnings)           => true
      case (w: GrantedWithWarnings, d: Declined)           => true
      case (w: GrantedWithWarnings, g: Granted)            => true
      case (g: Granted, d: Declined)                       => true
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

  import play.api.libs.json._
  import uk.gov.hmrc.play.json.Union

  implicit val keyReadsQuestionnaireId: KeyReads[Questionnaire.Id]   = KeyReads(key => JsSuccess(Questionnaire.Id(key)))
  implicit val keyWritesQuestionnaireId: KeyWrites[Questionnaire.Id] = KeyWrites(_.value)

  implicit val stateWrites: Writes[QuestionnaireState] = Writes {
    case QuestionnaireState.NotStarted    => JsString("NotStarted")
    case QuestionnaireState.InProgress    => JsString("InProgress")
    case QuestionnaireState.NotApplicable => JsString("NotApplicable")
    case QuestionnaireState.Completed     => JsString("Completed")
  }

  implicit val stateReads: Reads[QuestionnaireState] = Reads {
    case JsString("NotStarted")    => JsSuccess(QuestionnaireState.NotStarted)
    case JsString("InProgress")    => JsSuccess(QuestionnaireState.InProgress)
    case JsString("NotApplicable") => JsSuccess(QuestionnaireState.NotApplicable)
    case JsString("Completed")     => JsSuccess(QuestionnaireState.Completed)
    case _                         => JsError("Failed to parse QuestionnaireState value")
  }

  implicit val questionnaireProgressFormat: OFormat[QuestionnaireProgress] = Json.format[QuestionnaireProgress]

  implicit val questionIdsOfInterestFormat: OFormat[QuestionIdsOfInterest] = Json.format[QuestionIdsOfInterest]

  implicit val utcReads: Reads[Instant] = InstantJsonFormatter.lenientInstantReads

  import Submission.Status._

  implicit val rejectedStatusFormat: OFormat[Declined]                                         = Json.format[Declined]
  implicit val acceptedStatusFormat: OFormat[Granted]                                          = Json.format[Granted]
  implicit val acceptedWithWarningsStatusFormat: OFormat[GrantedWithWarnings]                  = Json.format[GrantedWithWarnings]
  implicit val failedStatusFormat: OFormat[Failed]                                             = Json.format[Failed]
  implicit val warningsStatusFormat: OFormat[Warnings]                                         = Json.format[Warnings]
  implicit val pendingResponsibleIndividualStatusFormat: OFormat[PendingResponsibleIndividual] = Json.format[PendingResponsibleIndividual]
  implicit val submittedStatusFormat: OFormat[Submitted]                                       = Json.format[Submitted]
  implicit val answeringStatusFormat: OFormat[Answering]                                       = Json.format[Answering]
  implicit val createdStatusFormat: OFormat[Created]                                           = Json.format[Created]

  implicit val submissionStatus: OFormat[Submission.Status] = Union.from[Submission.Status]("Submission.StatusType")
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

  import GroupOfQuestionnaires._
  import Question._

  implicit val submissionInstanceFormat: OFormat[Submission.Instance] = Json.format[Submission.Instance]
  implicit val submissionFormat: OFormat[Submission]                  = Json.format[Submission]
  implicit val extendedSubmissionFormat: OFormat[ExtendedSubmission]  = Json.format[ExtendedSubmission]
  implicit val markedSubmissionFormat: OFormat[MarkedSubmission]      = Json.format[MarkedSubmission]

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
