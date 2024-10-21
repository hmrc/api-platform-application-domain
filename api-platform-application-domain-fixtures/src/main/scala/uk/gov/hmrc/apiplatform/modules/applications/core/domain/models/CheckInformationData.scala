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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddressData
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

import uk.gov.hmrc.apiplatform.modules.applications.common.domain.models.FullNameData

object CheckInformationData extends FixedClock {
  val default = CheckInformation()

  val one = CheckInformation(
    contactDetails = Some(
      ContactDetails(
        fullname = FullNameData.one,
        email = LaxEmailAddressData.one,
        telephoneNumber = "020 1122 3344"
      )
    ),
    confirmedName = true,
    providedPrivacyPolicyURL = true,
    providedTermsAndConditionsURL = true,
    applicationDetails = Some(""),
    termsOfUseAgreements = List(
      TermsOfUseAgreement(
        emailAddress = LaxEmailAddressData.two,
        timeStamp = instant,
        version = "1.0"
      )
    )
  )
}

trait CheckInformationFixtures {
  val defaultCheckInformation = CheckInformationData.default
  val checkInformationOne     = CheckInformationData.one
}
