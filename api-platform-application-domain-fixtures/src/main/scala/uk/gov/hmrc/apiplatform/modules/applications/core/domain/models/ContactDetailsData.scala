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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.common.domain.models._

object ContactDetailsData {
  val one = ContactDetails(FullNameData.one, LaxEmailAddressData.one, "1234567890")
  val two = ContactDetails(FullNameData.two, LaxEmailAddressData.two, "0987654321")
}

trait ContactDetailsFixtures extends FullNameFixtures with LaxEmailAddressFixtures {
  val contactDetailsOne = ContactDetailsData.one
  val contactDetailsTwo = ContactDetailsData.two
}
