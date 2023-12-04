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

package uk.gov.hmrc.apiplatform.modules.applications.access.domain.models

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class OverrideTypeSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "OverrideType" should {
    val values =
      Table(
        ("Type", "text"),
        (OverrideType.GRANT_WITHOUT_TAXPAYER_CONSENT, "grant_without_taxpayer_consent"),
        (OverrideType.PERSIST_LOGIN_AFTER_GRANT, "persist_login_after_grant"),
        (OverrideType.SUPPRESS_IV_FOR_AGENTS, "suppress_iv_for_agents"),
        (OverrideType.SUPPRESS_IV_FOR_INDIVIDUALS, "suppress_iv_for_individuals"),
        (OverrideType.SUPPRESS_IV_FOR_ORGANISATIONS, "suppress_iv_for_organisations"),
        (OverrideType.ORIGIN_OVERRIDE, "origin_override")
      )

    "convert to string correctly" in {
      forAll(values) { (overrideType, text) =>
        overrideType.toString shouldBe text.toUpperCase()
      }
    }

    "displayText correctly" in {
      val displayTexts =
        Table(
          ("overrideType", "displayText"),
          (OverrideType.GRANT_WITHOUT_TAXPAYER_CONSENT, "Grant without taxpayer consent"),
          (OverrideType.PERSIST_LOGIN_AFTER_GRANT, "Persist login after grant"),
          (OverrideType.SUPPRESS_IV_FOR_AGENTS, "Suppress IV for agents"),
          (OverrideType.SUPPRESS_IV_FOR_INDIVIDUALS, "Suppress IV for individuals"),
          (OverrideType.SUPPRESS_IV_FOR_ORGANISATIONS, "Suppress IV for organisations"),
          (OverrideType.ORIGIN_OVERRIDE, "Origin override")
        )
      forAll(displayTexts) { (overrideType, displayText) =>
        overrideType.displayText shouldBe displayText
      }
    }

    "convert lower case string to case object" in {
      forAll(values) { (s, t) =>
        OverrideType.apply(t) shouldBe Some(s)
        OverrideType.unsafeApply(t) shouldBe s
      }
    }

    "convert mixed case string to case object" in {
      forAll(values) { (s, t) =>
        OverrideType.apply(t.toUpperCase()) shouldBe Some(s)
        OverrideType.unsafeApply(t.toUpperCase()) shouldBe s
      }
    }

    "convert string value to None when undefined or empty" in {
      OverrideType.apply("rubbish") shouldBe None
      OverrideType.apply("") shouldBe None
    }

    "throw when string value is invalid" in {
      intercept[RuntimeException] {
        OverrideType.unsafeApply("rubbish")
      }.getMessage() should include("Override Type")
    }

    "read from Json" in {
      forAll(values) { (s, t) =>
        testFromJson[OverrideType](s""" "$t" """)(s)
      }
    }

    "read with error from Json" in {
      intercept[Exception] {
        testFromJson[OverrideType](s"""123""")(OverrideType.GRANT_WITHOUT_TAXPAYER_CONSENT)
      }.getMessage() should include("Cannot parse Override Type from '123'")
    }

    "write to Json" in {
      forAll(values) { (s, t) =>
        Json.toJson[OverrideType](s) shouldBe JsString(t.toUpperCase())
      }
    }
  }
}
