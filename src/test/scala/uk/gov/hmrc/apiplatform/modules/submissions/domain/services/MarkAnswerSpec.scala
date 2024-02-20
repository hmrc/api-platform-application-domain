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

package uk.gov.hmrc.apiplatform.modules.submissions.domain.services

import scala.collection.immutable.ListMap

import cats.data.NonEmptyList

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.SubmissionId
import uk.gov.hmrc.apiplatform.modules.submissions.domain.models._

class MarkAnswerSpec extends HmrcSpec with FixedClock {

  object TestQuestionnaires {
    val question1Id      = Question.Id.random
    val question2Id      = Question.Id.random
    val questionnaireAId = Questionnaire.Id.random
    val submissionId     = SubmissionId.random
    val applicationId    = ApplicationId.random

    val standardContext: AskWhen.Context = Map(
      AskWhen.Context.Keys.IN_HOUSE_SOFTWARE       -> "No",
      AskWhen.Context.Keys.VAT_OR_ITSA             -> "No",
      AskWhen.Context.Keys.NEW_TERMS_OF_USE_UPLIFT -> "No"
    )

    val testQuestionIdsOfInterest = QuestionIdsOfInterest(
      responsibleIndividualIsRequesterId = Question.Id.random,
      responsibleIndividualNameId = Question.Id.random,
      responsibleIndividualEmailId = Question.Id.random,
      applicationNameId = Question.Id.random,
      privacyPolicyId = Question.Id.random,
      privacyPolicyUrlId = Question.Id.random,
      termsAndConditionsId = Question.Id.random,
      termsAndConditionsUrlId = Question.Id.random,
      organisationUrlId = Question.Id.random,
      identifyYourOrganisationId = Question.Id.random,
      serverLocationsId = Question.Id.random
    )

    val YES = SingleChoiceAnswer("Yes")
    val NO  = SingleChoiceAnswer("No")

    val ANSWER_FAIL = "a1"
    val ANSWER_WARN = "a2"
    val ANSWER_PASS = "a3"

    def buildSubmissionFromQuestions(questions: Question*) = {
      val questionnaire = Questionnaire(
        id = questionnaireAId,
        label = Questionnaire.Label("Questionnaie"),
        questions = NonEmptyList.fromListUnsafe(questions.map((q: Question) => QuestionItem(q)).toList)
      )

      val oneGroups = NonEmptyList.of(GroupOfQuestionnaires("Group", NonEmptyList.of(questionnaire)))
      Submission.create("bob@example.com", submissionId, applicationId, instant, oneGroups, testQuestionIdsOfInterest, standardContext)
    }

    def buildYesNoQuestion(id: Question.Id, yesMark: Mark, noMark: Mark) = YesNoQuestion(
      id,
      Wording("wording1"),
      Some(Statement(StatementText("Statement1"))),
      None,
      None,
      None,
      yesMark,
      noMark
    )

    def buildTextQuestion(id: Question.Id) = TextQuestion(
      id,
      Wording("wording1"),
      Some(Statement(StatementText("Statement1"), StatementBullets(CompoundFragment(StatementText("text "), StatementLink("link", "/example/url"))))),
      absence = Some(("blah blah blah", Fail)),
      errorInfo = Some(ErrorInfo("error"))
    )

    def buildAcknowledgementOnlyQuestion(id: Question.Id) = AcknowledgementOnly(
      id,
      Wording("wording1"),
      Some(Statement(StatementText("Statement1")))
    )

    def buildMultiChoiceQuestion(id: Question.Id, answerMap: ListMap[PossibleAnswer, Mark]) = MultiChoiceQuestion(
      id,
      Wording("wording1"),
      Some(Statement(StatementText("Statement1"))),
      None,
      None,
      None,
      answerMap
    )

    object YesNoQuestionnaireData {
      val question1 = buildYesNoQuestion(question1Id, Pass, Warn)
      val question2 = buildYesNoQuestion(question2Id, Pass, Warn)

      val submission = buildSubmissionFromQuestions(question1, question2)
    }

    object OptionalQuestionnaireData {
      val question1 = buildTextQuestion(question1Id)

      val submission = buildSubmissionFromQuestions(question1)
    }

    object AcknowledgementOnlyQuestionnaireData {
      val question1 = buildAcknowledgementOnlyQuestion(question1Id)

      val submission = buildSubmissionFromQuestions(question1)
    }

    object MultiChoiceQuestionnaireData {
      val question1 = buildMultiChoiceQuestion(question1Id, ListMap(PossibleAnswer(ANSWER_PASS) -> Pass, PossibleAnswer(ANSWER_WARN) -> Warn, PossibleAnswer(ANSWER_FAIL) -> Fail))

      val submission = buildSubmissionFromQuestions(question1)
    }
  }

  import TestQuestionnaires._

  def withYesNoAnswers(answer1: SingleChoiceAnswer, answer2: SingleChoiceAnswer): Submission = {
    require(List(YES, NO).contains(answer1))
    require(List(YES, NO).contains(answer2))

    hasCompletelyAnsweredWith(Map(question1Id -> answer1, question2Id -> answer2))(YesNoQuestionnaireData.submission)
  }

  def withSingleOptionalQuestionNoAnswer(): Submission = {
    hasCompletelyAnsweredWith(Map(question1Id -> NoAnswer))(OptionalQuestionnaireData.submission)
  }

  def withSingleOptionalQuestionAndAnswer(): Submission = {
    hasCompletelyAnsweredWith(Map(question1Id -> TextAnswer("blah blah")))(OptionalQuestionnaireData.submission)
  }

  def withAcknowledgementOnlyAnswers(): Submission = {
    hasCompletelyAnsweredWith(Map(question1Id -> AcknowledgedAnswer))(AcknowledgementOnlyQuestionnaireData.submission)
  }

  def withMultiChoiceAnswers(answers: String*): Submission = {
    hasCompletelyAnsweredWith(Map(question1Id -> MultipleChoiceAnswer(answers.toList.toSet)))(MultiChoiceQuestionnaireData.submission)
  }

  def hasCompletelyAnsweredWith(answers: Submission.AnswersToQuestions)(submission: Submission): Submission = {
    (
      Submission.addStatusHistory(Submission.Status.Answering(instant, true)) andThen
        Submission.updateLatestAnswersTo(answers)
    )(submission)
  }

  "markSubmission" should {
    "not accept incomplete submissions without throwing exception" in {
      intercept[IllegalArgumentException] {
        MarkAnswer.markSubmission(MultiChoiceQuestionnaireData.submission)
      }
    }

    "return Fail for NoAnswer in optional text question" in {
      val markedQuestions = MarkAnswer.markSubmission(withSingleOptionalQuestionNoAnswer())

      markedQuestions shouldBe Map(question1Id -> Fail)
    }

    "return Pass for Answer in optional text question" in {
      val markedQuestions = MarkAnswer.markSubmission(withSingleOptionalQuestionAndAnswer())

      markedQuestions shouldBe Map(question1Id -> Pass)
    }

    "return the correct marks for Single Choice questions" in {
      val markedQuestions = MarkAnswer.markSubmission(withYesNoAnswers(YES, NO))

      markedQuestions shouldBe Map(question1Id -> Pass, question2Id -> Warn)
    }

    "return the correct mark for AcknowledgementOnly question" in {
      val markedQuestions = MarkAnswer.markSubmission(withAcknowledgementOnlyAnswers())

      markedQuestions shouldBe Map(question1Id -> Pass)
    }

    "return Fail for Multiple Choice question" in {
      val markedQuestions = MarkAnswer.markSubmission(withMultiChoiceAnswers(ANSWER_FAIL))

      markedQuestions shouldBe Map(question1Id -> Fail)
    }

    "return Warn for Multiple Choice question" in {
      val markedQuestions = MarkAnswer.markSubmission(withMultiChoiceAnswers(ANSWER_WARN))

      markedQuestions shouldBe Map(question1Id -> Warn)
    }

    "return Pass for Multiple Choice question" in {
      val markedQuestions = MarkAnswer.markSubmission(withMultiChoiceAnswers(ANSWER_PASS))

      markedQuestions shouldBe Map(question1Id -> Pass)
    }

    "return Fail for Multiple Choice question if answer includes a single failure for the first answer" in {
      val markedQuestions = MarkAnswer.markSubmission(withMultiChoiceAnswers(ANSWER_FAIL, ANSWER_WARN, ANSWER_PASS))

      markedQuestions shouldBe Map(question1Id -> Fail)
    }

    "return Fail for Multiple Choice question if answer includes a single failure for the last answer" in {
      val markedQuestions = MarkAnswer.markSubmission(withMultiChoiceAnswers(ANSWER_PASS, ANSWER_WARN, ANSWER_FAIL))

      markedQuestions shouldBe Map(question1Id -> Fail)
    }

    "return Warn for Multiple Choice question if answer includes a single warnng and no failure" in {
      val markedQuestions = MarkAnswer.markSubmission(withMultiChoiceAnswers(ANSWER_WARN, ANSWER_PASS))

      markedQuestions shouldBe Map(question1Id -> Warn)
    }
  }
}
