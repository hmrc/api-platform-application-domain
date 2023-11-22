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

package uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.applications.common.domain.models.FullName
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.ResponsibleIndividual

class ResponsibleIndividualSpec extends BaseJsonFormattersSpec {
  import ResponsibleIndividualSpec._

  "ResponsibleIndividual" should {
    "build from raw text" in {
      ResponsibleIndividual.build("Fred Flintstone", "fred@bedrock.com") shouldBe example
    }
    "convert to json" in {
      Json.toJson[ResponsibleIndividual](example).toString shouldBe jsonText
    }

    "read from json" in {
      testFromJson[ResponsibleIndividual](jsonText)(example)
    }
  }
}

object ResponsibleIndividualSpec {
  val example  = ResponsibleIndividual(FullName("Fred Flintstone"), LaxEmailAddress("fred@bedrock.com"))
  val jsonText = """{"fullName":"Fred Flintstone","emailAddress":"fred@bedrock.com"}"""
}
