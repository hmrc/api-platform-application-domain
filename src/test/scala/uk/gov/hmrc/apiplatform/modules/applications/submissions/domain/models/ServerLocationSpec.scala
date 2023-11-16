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

package uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json.Json

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ServerLocationSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "ServerLocation" should {
    val values =
      Table(
        ("Type", "text"),
        (ServerLocation.InUK, "inUK"),
        (ServerLocation.InEEA, "inEEA"),
        (ServerLocation.OutsideEEAWithAdequacy, "outsideEEAWithAdequacy"),
        (ServerLocation.OutsideEEAWithoutAdequacy, "outsideEEAWithoutAdequacy")
      )

    "convert to string correctly" in {
      forAll(values) { (s, t) =>
        s.toString().toUpperCase shouldBe t.toUpperCase()
      }
    }

    "convert lower case string to case object" in {
      forAll(values) { (s, t) =>
        ServerLocation.apply(t) shouldBe Some(s)
        ServerLocation.unsafeApply(t) shouldBe s
      }
    }

    "convert mixed case string to case object" in {
      forAll(values) { (s, t) =>
        ServerLocation.apply(t.toUpperCase()) shouldBe Some(s)
        ServerLocation.unsafeApply(t.toUpperCase()) shouldBe s
      }
    }

    "convert string value to None when undefined or empty" in {
      ServerLocation.apply("rubbish") shouldBe None
      ServerLocation.apply("") shouldBe None
    }

    "throw when string value is invalid" in {
      intercept[RuntimeException] {
        ServerLocation.unsafeApply("rubbish")
      }.getMessage() should include("Server Location")
    }

    "read from Json" in {
      forAll(values) { (s, t) =>
        testFromJson[ServerLocation](s""" {"serverLocation": "$t"} """)(s)
      }
    }

    "read with error from Json" in {
      intercept[Exception] {
        testFromJson[ServerLocation](s""" "inUK" """)(ServerLocation.InEEA)
      }.getMessage() should include("inUK")
    }

    "write to Json" in {
      forAll(values) { (s, t) =>
        Json.toJson[ServerLocation](s) shouldBe Json.obj("serverLocation" -> t)
      }
    }
  }
}
