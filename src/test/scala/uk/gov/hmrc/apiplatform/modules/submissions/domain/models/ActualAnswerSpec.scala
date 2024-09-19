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

package uk.gov.hmrc.apiplatform.modules.submissions.domain.services

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

import uk.gov.hmrc.apiplatform.modules.submissions.domain.models._

class ActualAnswerSpec extends HmrcSpec with TableDrivenPropertyChecks {

  import ActualAnswer._

  val table = Table(
    ("ActualAnswer", "Expected Json"),
    (ActualAnswer.SingleChoiceAnswer("abc"), Json.parse("""{"answerType":"singleChoice","value":"abc"}""")),
    (ActualAnswer.MultipleChoiceAnswer(Set("Bobby", "Freddy")), Json.parse("""{"answerType":"multipleChoice","values":["Bobby","Freddy"]}""")),
    (ActualAnswer.AcknowledgedAnswer, Json.parse("""{"answerType":"acknowledged"}""")),
    (ActualAnswer.NoAnswer, Json.parse("""{"answerType":"noAnswer"}"""))
  )

  "Can format" should {
    "convert to and from all types" in {
      forAll(table) {
        case (answer, json) =>
          val jsvalue = Json.toJson[ActualAnswer](answer)
          jsvalue shouldBe json
          Json.fromJson[ActualAnswer](jsvalue).get shouldBe answer
      }
    }
  }
}
