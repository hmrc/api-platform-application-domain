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

package uk.gov.hmrc.apiplatform.modules.common.domain.models

import play.api.libs.json._

import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec
import LaxEmailAddress.StringSyntax
class LaxEmailAddressSpec extends JsonFormattersSpec {

  val bobSmithEmailAddress = LaxEmailAddress("bob@smith.com")

  "LaxEmailAddress" when {
    "creating a lax email address" should {
      "normalise the text" in {
        LaxEmailAddress("BOB@smith.com").normalise().text shouldBe "bob@smith.com"
      }
    }
    "comparing case insensitive" should {
      "match" in {
        LaxEmailAddress("BOB@smith.com").equalsIgnoreCase(bobSmithEmailAddress) shouldBe true
      }
    }
    "format a lax email address" should {
      "produce json" in {
        Json.toJson(LaxEmailAddress("quark")) shouldBe JsString("quark")
      }

      "read json" in {
        Json.parse(""" "quark" """).as[LaxEmailAddress] shouldBe LaxEmailAddress("quark")
      }
    }

    "StringSyntax" should {
      "convert as string to a laxEmail Address" in {
        "bob@smith.com".toLaxEmail shouldBe bobSmithEmailAddress
      }
    }
  }
}
