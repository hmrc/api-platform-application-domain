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

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class StateSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "State" should {
    val values =
      Table(
        ("State", "text"),
        (State.Deleted, "deleted"),
        (State.PendingGatekeeperApproval, "pending_gatekeeper_approval"),
        (State.PendingRequesterVerification, "pending_requester_verification"),
        (State.PendingResponsibleIndividualVerification, "pending_responsible_individual_verification"),
        (State.PreProduction, "pre_production"),
        (State.Production, "production"),
        (State.Testing, "testing")
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
        testFromJson[State](s"""123""")(State.Deleted)
      }.getMessage() should include("Cannot parse State from '123'")
    }

    "write to Json" in {
      forAll(values) { (s, t) =>
        Json.toJson[State](s) shouldBe JsString(t.toUpperCase())
      }
    }
  }
}
