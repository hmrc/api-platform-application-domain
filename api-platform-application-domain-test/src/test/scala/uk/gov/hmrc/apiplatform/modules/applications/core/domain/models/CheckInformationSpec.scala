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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class CheckInformationSpec extends BaseJsonFormattersSpec {
  import CheckInformationSpec._

  "CheckInformation" should {
    "produce json" in {
      Json.toJson(example) shouldBe Json.parse(jsonText)
    }
    "produce alternate json" in {
      Json.toJson(alternateExample) shouldBe Json.parse(alternateWrittenJsonText)
    }

    "read json" in {
      testFromJson[CheckInformation](jsonText)(
        example
      )
    }
    "read alternate json" in {
      testFromJson[CheckInformation](alternateJsonText)(
        alternateExample
      )
    }
  }
}

object CheckInformationSpec {

  val example = CheckInformation(
    contactDetails = Some(ContactDetailsSpec.example),
    confirmedName = false,
    apiSubscriptionsConfirmed = true,
    apiSubscriptionConfigurationsConfirmed = false,
    providedPrivacyPolicyURL = true,
    providedTermsAndConditionsURL = true,
    applicationDetails = Some("Some details"),
    teamConfirmed = true,
    termsOfUseAgreements = List(TermsOfUseAgreementSpec.example)
  )

  val jsonText =
    s"""{"contactDetails": ${ContactDetailsSpec.jsonText},"confirmedName":false,"apiSubscriptionsConfirmed":true,"apiSubscriptionConfigurationsConfirmed":false,"providedPrivacyPolicyURL":true,"providedTermsAndConditionsURL":true,"applicationDetails":"Some details","teamConfirmed":true,"termsOfUseAgreements":[${TermsOfUseAgreementSpec.jsonText}]}"""

  val alternateExample = example.copy(termsOfUseAgreements = List.empty)

  val alternateJsonText =
    s"""{"contactDetails": ${ContactDetailsSpec.jsonText},"confirmedName":false,"apiSubscriptionsConfirmed":true,"apiSubscriptionConfigurationsConfirmed":false,"providedPrivacyPolicyURL":true,"providedTermsAndConditionsURL":true,"applicationDetails":"Some details","teamConfirmed":true}"""

  val alternateWrittenJsonText =
    s"""{"contactDetails": ${ContactDetailsSpec.jsonText},"confirmedName":false,"apiSubscriptionsConfirmed":true,"apiSubscriptionConfigurationsConfirmed":false,"providedPrivacyPolicyURL":true,"providedTermsAndConditionsURL":true,"applicationDetails":"Some details","teamConfirmed":true, "termsOfUseAgreements":[]}"""
}
