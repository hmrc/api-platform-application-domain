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

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues
import org.scalatest.AppendedClues

class RedirectUriSpec extends AnyWordSpec with Matchers with OptionValues with AppendedClues {
  "redirectUri validation" should {
    val invalidCases = Map(
      "fragment in http url"      -> "http://example.com#test",
      "fragment in https url"     -> "https://example.com#test",
      "fragment in localhost url" -> "http://localhost#test",
      "invalid url"               -> "random",
      "not https"                 -> "http://example.com",
      "invalid localhost"         -> "http://localhost.example.com"
    )

    val validCases = Map(
      "localhost"           -> "http://localhost",
      "localhost with port" -> "http://localhost:8080",
      "localhost with path" -> "http://localhost:8080/some/path",
      "https url"           -> "https://example.com",
      "oob"                 -> "urn:ietf:wg:oauth:2.0:oob",
      "oob auto"            -> "urn:ietf:wg:oauth:2.0:oob:auto"
    )

    for ((k, v) <- invalidCases) {
      s"reject redirect uri for $k url" in {
        RedirectUri(v) shouldBe None withClue(s"$k: $v should be Invalid")
      }
    }
    
    for ((k, v) <- validCases) {
      s"accept redirect uri for $k" in {
        RedirectUri(v).value.uri shouldBe v withClue(s"$k: $v should be Valid")
      }
    }
  }
}