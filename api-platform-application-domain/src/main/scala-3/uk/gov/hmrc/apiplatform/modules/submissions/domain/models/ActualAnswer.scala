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

sealed trait ActualAnswer

object ActualAnswer {

  case class MultipleChoiceAnswer(values: Set[String]) extends ActualAnswer
  case class SingleChoiceAnswer(value: String)         extends ActualAnswer
  case class TextAnswer(value: String)                 extends ActualAnswer
  case object AcknowledgedAnswer                       extends ActualAnswer
  case object NoAnswer                                 extends ActualAnswer

  import play.api.libs.json.*
  import uk.gov.hmrc.play.json.Union

  given OFormat[TextAnswer]           = Json.format[TextAnswer]
  given OFormat[SingleChoiceAnswer]   = Json.format[SingleChoiceAnswer]
  given OFormat[MultipleChoiceAnswer] = Json.format[MultipleChoiceAnswer]

  given OFormat[ActualAnswer] = Union.from[ActualAnswer]("answerType")
    .and[MultipleChoiceAnswer]("multipleChoice")
    .and[SingleChoiceAnswer]("singleChoice")
    .and[TextAnswer]("text")
    .andType("acknowledged", () => AcknowledgedAnswer)
    .andType("noAnswer", () => NoAnswer)
    .format

}
