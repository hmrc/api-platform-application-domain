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

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json.{JsString, Json}

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class StateSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "State" should {
    val values =
      Table(
        ("State", "text"),
        (State.DELETED, "deleted"),
        (State.PENDING_GATEKEEPER_APPROVAL, "pending_gatekeeper_approval"),
        (State.PENDING_REQUESTER_VERIFICATION, "pending_requester_verification"),
        (State.PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION, "pending_responsible_individual_verification"),
        (State.PRE_PRODUCTION, "pre_production"),
        (State.PRODUCTION, "production"),
        (State.TESTING, "testing")
      )

    "convert to string correctly" in {
      forAll(values) { (s, t) =>
        s.toString() shouldBe t.toUpperCase()
      }
    }

    "convert lower case string to case object" in {
      forAll(values) { (s, t) =>
        State.apply(t) shouldBe Some(s)
        State.unsafeApply(t) shouldBe s
      }
    }

    "convert mixed case string to case object" in {
      forAll(values) { (s, t) =>
        State.apply(t.toUpperCase()) shouldBe Some(s)
        State.unsafeApply(t.toUpperCase()) shouldBe s
      }
    }

    "convert string value to None when undefined or empty" in {
      State.apply("rubbish") shouldBe None
      State.apply("") shouldBe None
    }

    "throw when string value is invalid" in {
      intercept[RuntimeException] {
        State.unsafeApply("rubbish")
      }.getMessage() should include("State")
    }

    "read from Json" in {
      forAll(values) { (s, t) =>
        testFromJson[State](s""" "$t" """)(s)
      }
    }

    "read with error from Json" in {
      intercept[Exception] {
        testFromJson[State](s"""123""")(State.DELETED)
      }.getMessage() should include("Cannot parse State from '123'")
    }

    "write to Json" in {
      forAll(values) { (s, t) =>
        Json.toJson[State](s) shouldBe JsString(t.toUpperCase())
      }
    }
  }
}
