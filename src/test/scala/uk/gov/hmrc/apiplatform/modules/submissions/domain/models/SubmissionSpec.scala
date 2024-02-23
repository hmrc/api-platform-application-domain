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

package uk.gov.hmrc.apiplatform.modules.submissions.domain.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import uk.gov.hmrc.apiplatform.modules.submissions.utils.SubmissionsTestData

class SubmissionSpec extends AnyWordSpec with Matchers with SubmissionsTestData {

  "submission questionIdsOfInterest app name" in {
    Submission.updateLatestAnswersTo(samplePassAnswersToQuestions)(aSubmission).latestInstance.answersToQuestions(
      aSubmission.questionIdsOfInterest.applicationNameId
    ) shouldBe TextAnswer("name of software")
  }

  "submission instance state history" in {
    aSubmission.latestInstance.statusHistory.head.isOpenToAnswers shouldBe true
    aSubmission.latestInstance.isOpenToAnswers shouldBe true
    aSubmission.status.isOpenToAnswers shouldBe true
  }

  "submission instance is in progress" in {
    aSubmission.latestInstance.isOpenToAnswers shouldBe true
  }

  "submission is in progress" in {
    aSubmission.status.isOpenToAnswers shouldBe true
  }

  "submission findQuestionnaireContaining" in {
    aSubmission.findQuestionnaireContaining(aSubmission.questionIdsOfInterest.applicationNameId) shouldBe Some(CustomersAuthorisingYourSoftware.questionnaire)
  }

  "submission setLatestAnswers" in {
    val newAnswersToQuestions = Map(
      (OrganisationDetails.question1.id -> TextAnswer("new web site"))
    )

    Submission.updateLatestAnswersTo(newAnswersToQuestions)(aSubmission).latestInstance.answersToQuestions(OrganisationDetails.question1.id) shouldBe TextAnswer("new web site")
  }

  "submission automaticallyMark pass" in {
    val answering1       = Submission.addStatusHistory(Submission.Status.Answering(instant, false))(aSubmission)
    val answering2       = Submission.updateLatestAnswersTo(samplePassAnswersToQuestions)(answering1)
    val answered         = Submission.addStatusHistory(Submission.Status.Answering(instant, true))(answering2)
    val submitted        = Submission.submit(instant, "bob@example.com")(answered)
    val markedSubmission = Submission.automaticallyMark(instant, "bob@example.com")(submitted)
    markedSubmission.latestInstance.isGranted shouldBe true
    markedSubmission.latestInstance.status shouldBe Submission.Status.Granted(instant, "bob@example.com", Some("Automatically passed"), None)
  }

  "submission automaticallyMark fail" in {
    val answering1       = Submission.addStatusHistory(Submission.Status.Answering(instant, false))(aSubmission)
    val answering2       = Submission.updateLatestAnswersTo(sampleFailAnswersToQuestions)(answering1)
    val answered         = Submission.addStatusHistory(Submission.Status.Answering(instant, true))(answering2)
    val submitted        = Submission.submit(instant, "bob@example.com")(answered)
    val markedSubmission = Submission.automaticallyMark(instant, "bob@example.com")(submitted)
    markedSubmission.latestInstance.isFailed shouldBe true
    markedSubmission.latestInstance.status shouldBe Submission.Status.Failed(instant, "bob@example.com")
  }

  "submission automaticallyMark warning" in {
    val answering1       = Submission.addStatusHistory(Submission.Status.Answering(instant, false))(aSubmission)
    val answering2       = Submission.updateLatestAnswersTo(sampleWarningsAnswersToQuestions)(answering1)
    val answered         = Submission.addStatusHistory(Submission.Status.Answering(instant, true))(answering2)
    val submitted        = Submission.submit(instant, "bob@example.com")(answered)
    val markedSubmission = Submission.automaticallyMark(instant, "bob@example.com")(submitted)
    markedSubmission.latestInstance.isWarnings shouldBe true
    markedSubmission.latestInstance.status shouldBe Submission.Status.Warnings(instant, "bob@example.com")
  }

  "questionnaire state description" in {
    QuestionnaireState.describe(QuestionnaireState.NotStarted) shouldBe "Not Started"
    QuestionnaireState.describe(QuestionnaireState.InProgress) shouldBe "In Progress"
    QuestionnaireState.describe(QuestionnaireState.NotApplicable) shouldBe "Not Applicable"
    QuestionnaireState.describe(QuestionnaireState.Completed) shouldBe "Completed"
  }

  "questionnaire state is completed" in {
    QuestionnaireState.isCompleted(QuestionnaireState.NotStarted) shouldBe false
    QuestionnaireState.isCompleted(QuestionnaireState.InProgress) shouldBe false
    QuestionnaireState.isCompleted(QuestionnaireState.Completed) shouldBe true
  }

  "question absence text" in {
    OrganisationDetails.questionRI2.absenceText shouldBe None
    OrganisationDetails.questionRI2.absenceMark shouldBe None
    OrganisationDetails.questionRI2.isOptional shouldBe false
    OrganisationDetails.question1.absenceText shouldBe Some("My organisation doesn't have a website")
    OrganisationDetails.question1.absenceMark shouldBe Some(Fail)
    OrganisationDetails.question1.isOptional shouldBe true
  }

  "shouldAsk" in {
    AskWhen.shouldAsk(standardContext, answersToQuestions)(OrganisationDetails.questionnaire.questions.head.askWhen) shouldBe true
    AskWhen.shouldAsk(standardContext, answersToQuestions)(OrganisationDetails.questionnaire.questions.tail.head.askWhen) shouldBe true
    AskWhen.shouldAsk(standardContext, answersToQuestions)(CustomersAuthorisingYourSoftware.questionnaire.questions.tail.tail.head.askWhen) shouldBe true
  }

  "submission status isOpenToAnswers" in {
    Submission.Status.Answering(instant, false).isOpenToAnswers shouldBe true
  }

  "submission status isCreated" in {
    Submission.Status.Answering(instant, false).isCreated shouldBe false
  }

  "submission status isGrantedWithOrWithoutWarnings" in {
    Submission.Status.Answering(instant, false).isGrantedWithOrWithoutWarnings shouldBe false
    Submission.Status.Granted(instant, "bob@example.com", None, None).isGrantedWithOrWithoutWarnings shouldBe true
    Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None).isGrantedWithOrWithoutWarnings shouldBe true
  }

  "submission status isGrantedWithWarnings" in {
    Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None).isGrantedWithWarnings shouldBe true
  }

  "submission status isDeclined" in {
    Submission.Status.Declined(instant, "bob@example.com", "reasons").isDeclined shouldBe true
  }

  "submission status isPendingResponsibleIndividual" in {
    Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com").isPendingResponsibleIndividual shouldBe true
  }

  "submission status isLegalTransition" in {
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Failed(instant, "bob@example.com")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Warnings(instant, "bob@example.com")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Granted(instant, "bob@example.com", Some("comments"), None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Declined(instant, "bob@example.com", "reasons")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Failed(instant, "bob@example.com"),
      Submission.Status.Granted(instant, "bob@example.com", Some("comments"), None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Failed(instant, "bob@example.com"),
      Submission.Status.Declined(instant, "bob@example.com", "reasons")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Warnings(instant, "bob@example.com"),
      Submission.Status.Granted(instant, "bob@example.com", Some("comments"), None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Warnings(instant, "bob@example.com"),
      Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None),
      Submission.Status.Declined(instant, "bob@example.com", "reasons")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None),
      Submission.Status.Granted(instant, "bob@example.com", None, None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Answering(instant, false),
      Submission.Status.Granted(instant, "bob@example.com", None, None)
    ) shouldBe false
  }
}
