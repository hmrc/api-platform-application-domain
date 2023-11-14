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

import play.api.libs.json.{JsString, Json}

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class SubmissionIdSpec extends BaseJsonFormattersSpec {
  val aSubmissionId = SubmissionId.random

  "SubmissionId" should {
    "toString" in {
      aSubmissionId.toString() shouldBe aSubmissionId.value.toString()
    }

    "apply raw text" in {
      val in = SubmissionId.random

      SubmissionId.apply(in.value.toString()) shouldBe Some(in)
    }

    "apply raw text fails when not valid" in {
      SubmissionId.apply("not-a-uuid") shouldBe None
    }

    "unsafeApply text" in {
      val in = SubmissionId.random

      SubmissionId.unsafeApply(in.value.toString()) shouldBe in
    }

    "unsafeApply raw text throws when not valid" in {
      intercept[RuntimeException] {
        SubmissionId.unsafeApply("not-a-uuid")
      }
    }

    "convert to json" in {
      Json.toJson(aSubmissionId) shouldBe JsString(aSubmissionId.value.toString())
    }

    "read from json" in {
      testFromJson[SubmissionId](s""""${aSubmissionId.toString}"""")(aSubmissionId)
    }
  }
}
