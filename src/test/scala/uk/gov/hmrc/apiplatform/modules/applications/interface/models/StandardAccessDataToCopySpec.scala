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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.OverrideFlag
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.RedirectUri

class StandardAccessDataToCopySpec extends BaseJsonFormattersSpec {
  import StandardAccessDataToCopySpec._

  "StandardAccessDataToCopy" should {
    "convert to json" in {
      Json.toJson[StandardAccessDataToCopy](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[StandardAccessDataToCopy](jsonText)(example)
    }
  }
}

object StandardAccessDataToCopySpec {

  val example  = StandardAccessDataToCopy(
    redirectUris = List(RedirectUri.unsafeApply("https://abc.com/abc")),
    overrides = Set(OverrideFlag.PersistLogin)
  )
  val jsonText = """{"redirectUris":["https://abc.com/abc"],"overrides":[{"overrideType":"PERSIST_LOGIN_AFTER_GRANT"}]}"""
}
