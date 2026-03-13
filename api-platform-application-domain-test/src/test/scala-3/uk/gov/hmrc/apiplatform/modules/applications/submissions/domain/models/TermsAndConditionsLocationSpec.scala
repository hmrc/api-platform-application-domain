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

package uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class TermsAndConditionsLocationSpec extends BaseJsonFormattersSpec {

  "TermsAndConditionsLocationJsonFormatters" when {

    "given a location of none provided" should {
      "produce json" in {
        testToJson[TermsAndConditionsLocation](TermsAndConditionsLocation.NoneProvided)(
          ("termsAndConditionsType" -> "noneProvided")
        )
      }

      "read json" in {
        testFromJson[TermsAndConditionsLocation]("""{"termsAndConditionsType":"noneProvided"}""")(
          TermsAndConditionsLocation.NoneProvided
        )
      }
    }

    "given location of in desktop software provided" should {
      "produce json" in {
        testToJson[TermsAndConditionsLocation](TermsAndConditionsLocation.InDesktopSoftware)(
          ("termsAndConditionsType" -> "inDesktop")
        )
      }

      "read json" in {
        testFromJson[TermsAndConditionsLocation]("""{"termsAndConditionsType":"inDesktop"}""")(
          TermsAndConditionsLocation.InDesktopSoftware
        )
      }
    }

    "given location of url provided" should {
      "produce json" in {
        testToJson[TermsAndConditionsLocation](TermsAndConditionsLocation.Url("aUrl"))(
          ("termsAndConditionsType" -> "url"),
          ("value"                  -> "aUrl")
        )
      }

      "read json" in {
        testFromJson[TermsAndConditionsLocation]("""{"termsAndConditionsType":"url","value":"aUrl"}""")(
          TermsAndConditionsLocation.Url("aUrl")
        )
      }
    }
  }

  "TermsAndConditionsLocations" should {
    "describe a location correctly" in {
      TermsAndConditionsLocation.InDesktopSoftware.describe() shouldBe "In desktop software"
      TermsAndConditionsLocation.NoneProvided.describe() shouldBe "None provided"
      TermsAndConditionsLocation.Url("http://yo.com").describe() shouldBe "http://yo.com"
    }
  }
}
