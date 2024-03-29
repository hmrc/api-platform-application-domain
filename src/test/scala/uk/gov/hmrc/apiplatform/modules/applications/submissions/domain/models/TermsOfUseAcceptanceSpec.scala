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

package uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models

import java.time.temporal.ChronoUnit

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

class TermsOfUseAcceptanceSpec extends BaseJsonFormattersSpec {
  import TermsOfUseAcceptanceSpec._

  "TermsOfUseAcceptance" should {
    "convert to json" in {
      Json.toJson[TermsOfUseAcceptance](example).toString shouldBe jsonText
    }

    "read from json" in {
      testFromJson[TermsOfUseAcceptance](jsonText)(example)
    }
  }
}

object TermsOfUseAcceptanceSpec extends FixedClock {
  val id      = SubmissionId.random
  val example = TermsOfUseAcceptance(ResponsibleIndividualSpec.example, instant.truncatedTo(ChronoUnit.SECONDS), id, 1)

  val jsonText =
    s"""{"responsibleIndividual":{"fullName":"Fred Flintstone","emailAddress":"fred@bedrock.com"},"dateTime":"2020-01-02T03:04:05Z","submissionId":"$id","submissionInstance":1}"""
}
