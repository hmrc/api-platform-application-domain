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

package uk.gov.hmrc.apiplatform.modules.applications.domain.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ContactDetailsSpec extends BaseJsonFormattersSpec {
  import ContactDetailsSpec._

  "ContactDetails" should {
    "produce json" in {
      testToJson[ContactDetails](example)(
        ("fullname"        -> "Fred Flintstone"),
        ("email"           -> email.text),
        ("telephoneNumber" -> "01234566789")
      )
    }

    "read json" in {
      testFromJson[ContactDetails](jsonText)(
        example
      )
    }
  }
}

object ContactDetailsSpec {
  val email    = LaxEmailAddress("fred@bedrock.com")
  val example  = ContactDetails(FullNameSpec.example, email, "01234566789")
  val jsonText = s"""{"fullname":"Fred Flintstone","email":"${email.text}","telephoneNumber":"01234566789"}"""
}
