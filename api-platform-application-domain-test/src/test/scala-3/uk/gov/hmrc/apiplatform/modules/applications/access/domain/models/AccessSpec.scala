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

package uk.gov.hmrc.apiplatform.modules.applications.access.domain.models

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{ImportantSubmissionDataData, PrivacyPolicyLocation, TermsAndConditionsLocation}

class AccessSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {
  import AccessSpec.*

  "Access" should {
    "correctly identify the AccessType" in {
      val values = Table(
        ("Access", "Expected Type"),
        (Access.Standard(), AccessType.Standard),
        (Access.Privileged(), AccessType.Privileged),
        (Access.Ropc(), AccessType.Ropc)
      )

      forAll(values) {
        case (access, accessType) => access.accessType shouldBe accessType
      }
    }

    val values = Table(
      ("Access", "Expected Json"),
      (Access.Standard(), emptyStandard),
      (Access.Privileged(), emptyPriviledged),
      (Access.Ropc(), emptyRopc)
    )

    "correctly write the default Access to Json" in {
      forAll(values) {
        (access, expectedJson) => Json.toJson[Access](access) shouldBe Json.parse(expectedJson)
      }
    }

    "correctly read the default Access to Json" in {
      forAll(values) {
        (access, expectedJson) => Json.parse(expectedJson).as[Access] shouldBe access
      }
    }

    "correctly find the privacy policy" in {
      val values = Table(
        ("submission data exists", "submission value", "direct url", "expectation"),
        (false, PrivacyPolicyLocation.InDesktopSoftware, None, PrivacyPolicyLocation.NoneProvided),
        (false, PrivacyPolicyLocation.InDesktopSoftware, Some("x"), PrivacyPolicyLocation.Url("x")),
        (true, PrivacyPolicyLocation.NoneProvided, None, PrivacyPolicyLocation.NoneProvided),
        (true, PrivacyPolicyLocation.InDesktopSoftware, None, PrivacyPolicyLocation.InDesktopSoftware),
        (true, PrivacyPolicyLocation.InDesktopSoftware, Some("x"), PrivacyPolicyLocation.InDesktopSoftware),
        (true, PrivacyPolicyLocation.Url("a"), Some("x"), PrivacyPolicyLocation.Url("a"))
      )

      def test(access: Access.Standard, expected: PrivacyPolicyLocation) = {
        access.privacyPolicyLocation shouldBe expected
      }

      forAll(values) {
        case (false, _, direct, expected)  =>
          val access = AccessData.Standard.default.copy(privacyPolicyUrl = direct, importantSubmissionData = None)
          test(access, expected)
        case (true, ppl, direct, expected) =>
          val access =
            AccessData.Standard.default.copy(privacyPolicyUrl = direct, importantSubmissionData = Some(ImportantSubmissionDataData.default.copy(privacyPolicyLocation = ppl)))
          test(access, expected)
      }
    }

    "correctly find the terms and conditions" in {
      val values = Table(
        ("submission data exists", "submission value", "direct url", "expectation"),
        (false, TermsAndConditionsLocation.InDesktopSoftware, None, TermsAndConditionsLocation.NoneProvided),
        (false, TermsAndConditionsLocation.InDesktopSoftware, Some("x"), TermsAndConditionsLocation.Url("x")),
        (true, TermsAndConditionsLocation.NoneProvided, None, TermsAndConditionsLocation.NoneProvided),
        (true, TermsAndConditionsLocation.InDesktopSoftware, None, TermsAndConditionsLocation.InDesktopSoftware),
        (true, TermsAndConditionsLocation.InDesktopSoftware, Some("x"), TermsAndConditionsLocation.InDesktopSoftware),
        (true, TermsAndConditionsLocation.Url("a"), Some("x"), TermsAndConditionsLocation.Url("a"))
      )

      def test(access: Access.Standard, expected: TermsAndConditionsLocation) = {
        access.termsAndConditionsLocation shouldBe expected
      }

      forAll(values) {
        case (false, _, direct, expected)  =>
          val access = AccessData.Standard.default.copy(termsAndConditionsUrl = direct, importantSubmissionData = None)
          test(access, expected)
        case (true, tnc, direct, expected) =>
          val access = AccessData.Standard.default.copy(
            termsAndConditionsUrl = direct,
            importantSubmissionData = Some(ImportantSubmissionDataData.default.copy(termsAndConditionsLocation = tnc))
          )
          test(access, expected)
      }
    }
  }
}

object AccessSpec {
  val emptyStandard    = """{"redirectUris":[],"postLogoutRedirectUris":[],"overrides":[],"accessType":"STANDARD"}"""
  val emptyPriviledged = """{"scopes":[],"accessType":"PRIVILEGED"}"""
  val emptyRopc        = """{"scopes":[],"accessType":"ROPC"}"""
}
