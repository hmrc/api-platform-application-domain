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

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.submissions.utils.SubmissionsTestData

class QuestionSpec extends BaseJsonFormattersSpec with SubmissionsTestData {

  def jsonTextQuestion(mark: String = "fail") = {
    s"""{
       |  "id" : "b9dbf0a5-e72b-4c89-a735-26f0858ca6cc",
       |  "wording" : "What is your organisation’s URL?",
       |  "hintText" : {
       |    "text" : "For example https://example.com",
       |    "statementType" : "text"
       |  },
       |  "validation" : {
       |    "validationType" : "url"
       |  },
       |  "absence" : [ "My organisation doesn't have a website", "$mark" ],
       |  "errorInfo" : {
       |    "summary" : "Enter a URL in the correct format, like https://example.com"
       |  }
       |}""".stripMargin
  }

  val jsonYesNoQuestion =
    """{
      |  "id" : "653d2ee4-09cf-46a0-bc73-350a385ae860",
      |  "wording" : "Do your development practices follow our guidance?",
      |  "statement" : {
      |    "fragments" : [ {
      |      "fragments" : [ {
      |        "text" : "You must develop software following our",
      |        "statementType" : "text"
      |      }, {
      |        "text" : "development practices (opens in a new tab)",
      |        "url" : "https://developer.service.hmrc.gov.uk/api-documentation/docs/development-practices",
      |        "statementType" : "link"
      |      }, {
      |        "text" : ".",
      |        "statementType" : "text"
      |      } ],
      |      "statementType" : "compound"
      |    } ]
      |  },
      |  "yesMarking" : "pass",
      |  "noMarking" : "warn"
      |}""".stripMargin

  val jsonChooseOneOfQuestion =
    """{
      |  "id" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
      |  "wording" : "Identify your organisation",
      |  "statement" : {
      |    "fragments" : [ {
      |      "text" : "Provide evidence that you or your organisation is officially registered in the UK. Choose one option.",
      |      "statementType" : "text"
      |    } ]
      |  },
      |  "marking" : [ {
      |    "Unique Taxpayer Reference (UTR)" : "pass"
      |  }, {
      |    "VAT registration number" : "pass"
      |  }, {
      |    "Corporation Tax Unique Taxpayer Reference (UTR)" : "pass"
      |  }, {
      |    "PAYE reference" : "pass"
      |  }, {
      |    "My organisation is in the UK and doesn't have any of these" : "pass"
      |  }, {
      |    "My organisation is outside the UK and doesn't have any of these" : "warn"
      |  } ],
      |  "errorInfo" : {
      |    "summary" : "Select a way to identify your organisation"
      |  }
      |}""".stripMargin

  "question absence text" in {
    OrganisationDetails.questionRI2.absenceText shouldBe None
    OrganisationDetails.question1.absenceText shouldBe Some("My organisation doesn't have a website")
  }

  "question absence mark" in {
    OrganisationDetails.questionRI2.absenceMark shouldBe None
    OrganisationDetails.question1.absenceMark shouldBe Some(Mark.Fail)
  }

  "question is optional" in {
    OrganisationDetails.questionRI2.isOptional shouldBe false
    OrganisationDetails.question1.isOptional shouldBe true
  }

  "question html value" in {
    OrganisationDetails.question2.choices.head.htmlValue shouldBe "Unique-Taxpayer-Reference-UTR"
  }

  "toJson for text question" in {
    Json.prettyPrint(Json.toJson(OrganisationDetails.question1)) shouldBe jsonTextQuestion()
  }

  "read text question from json" in {
    testFromJson[Question.TextQuestion](jsonTextQuestion())(OrganisationDetails.question1)
  }

  "read invalid text question from json" in {
    intercept[Exception] {
      testFromJson[Question.TextQuestion](jsonTextQuestion("invalid"))(OrganisationDetails.question1)
    }
  }

  "toJson for yesno question" in {
    Json.prettyPrint(Json.toJson(DevelopmentPractices.question1)) shouldBe jsonYesNoQuestion
  }

  "read yes no question from json" in {
    testFromJson[Question.YesNoQuestion](jsonYesNoQuestion)(DevelopmentPractices.question1)
  }

  "toJson for choose one of question" in {
    Json.prettyPrint(Json.toJson(OrganisationDetails.question2)) shouldBe jsonChooseOneOfQuestion
  }

  "read choose one of question from json" in {
    testFromJson[Question.ChooseOneOfQuestion](jsonChooseOneOfQuestion)(OrganisationDetails.question2)
  }
}
