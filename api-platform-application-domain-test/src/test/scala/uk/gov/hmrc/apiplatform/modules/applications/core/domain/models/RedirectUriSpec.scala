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

import org.scalatest.{AppendedClues, OptionValues}

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class RedirectUriSpec extends BaseJsonFormattersSpec with OptionValues with AppendedClues {

  "redirectUri validation" should {
    val invalidCases = Map(
      "fragment in http"      -> "http://example.com#test",
      "fragment in https"     -> "https://example.com#test",
      "fragment in localhost" -> "http://localhost#test",
      "invalid"               -> "random",
      "relative"              -> "/auth-redirect",
      "not https"             -> "http://example.com",
      "blank"                 -> "",
      "invalid localhost"     -> "http://localhost.example.com"
    )

    val validCases = Map(
      "localhost"           -> "http://localhost",
      "localhost with port" -> "http://localhost:8080",
      "localhost with path" -> "http://localhost:8080/some/path",
      "https url"           -> "https://example.com",
      "query param"         -> "https://www.example.com:8080/auth-redirect?some_parameter=some_value",
      "custom url"          -> "uk.gov.hmrc.app://oauth-authorization-code",
      "oob"                 -> "urn:ietf:wg:oauth:2.0:oob",
      "oob auto"            -> "urn:ietf:wg:oauth:2.0:oob:auto"
    )

    for ((k, v) <- invalidCases) {
      s"reject redirect uri for $k url" in {
        LoginRedirectUri(v) shouldBe None withClue (s"$k: $v should be Invalid")
      }
    }

    for ((k, v) <- validCases) {
      s"accept redirect uri for $k" in {
        LoginRedirectUri(v).value.uri shouldBe v withClue (s"$k: $v should be Valid")
      }
    }

    for ((k, v) <- validCases) {
      s"unsafeApply successfully $k" in {
        LoginRedirectUri.unsafeApply(v).uri shouldBe v withClue (s"$k: $v should be Valid")
      }
    }

    for ((k, v) <- invalidCases) {
      s"unsafeApply fail appropriately $k" in {
        intercept[RuntimeException] {
          LoginRedirectUri.unsafeApply(v).uri
        }
      }
    }

    import play.api.libs.json._
    val validLoginUri   = LoginRedirectUri.unsafeApply("https://abc.com/a")
    val invalidLoginUri = new LoginRedirectUri("broken")

    "convert to json" in {
      Json.toJson[LoginRedirectUri](validLoginUri) shouldBe JsString("https://abc.com/a")
      Json.toJson[LoginRedirectUri](invalidLoginUri) shouldBe JsString("broken")
    }

    "read from json" in {
      testFromJson[LoginRedirectUri](""" "broken" """)(invalidLoginUri)
      testFromJson[LoginRedirectUri](""" "https://abc.com/a" """)(validLoginUri)
    }

    "supports toString" in {
      val loginRedirectUri = LoginRedirectUri("https://localhost:101/abc").get
      s"$loginRedirectUri" shouldBe "https://localhost:101/abc"
    }
  }
}
