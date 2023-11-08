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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import java.time.LocalDateTime

import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class TermsOfUseAgreementSpec extends BaseJsonFormattersSpec {
  import TermsOfUseAgreementSpec._

  "TermsOfUseAgreement" should {
    "produce json" in {
      testToJson[TermsOfUseAgreement](example)(
        ("emailAddress" -> email.text),
        ("timeStamp"    -> "2020-01-02T03:04:05Z"),
        ("version"      -> "v1")
      )
    }

    "read json" in {
      testFromJson[TermsOfUseAgreement](jsonText)(
        example
      )
    }
  }
}

object TermsOfUseAgreementSpec {
  val email   = LaxEmailAddress("fred@bedrock.com")
  val now     = LocalDateTime.of(2020, 1, 2, 3, 4, 5)
  val example = TermsOfUseAgreement(email, now, "v1")

  val jsonText = s"""{"emailAddress":"${email.text}","timeStamp":"2020-01-02T03:04:05Z","version":"v1"}"""
}
