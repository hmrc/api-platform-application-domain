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

class AccessTypeSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "AccessType" should {
    val values =
      Table(
        ("Type", "text"),
        (AccessType.STANDARD, "standard"),
        (AccessType.PRIVILEGED, "privileged"),
        (AccessType.ROPC, "ropc")
      )

    "displayText correctly" in {
      AccessType.STANDARD.displayText shouldBe "Standard"
      AccessType.PRIVILEGED.displayText shouldBe "Privileged"
      AccessType.ROPC.displayText shouldBe "Ropc"
    }

    "convert to string correctly" in {
      forAll(values) { (s, t) =>
        s.toString() shouldBe t.toUpperCase()
      }
    }

    "convert lower case string to case object" in {
      forAll(values) { (s, t) =>
        AccessType.apply(t) shouldBe Some(s)
        AccessType.unsafeApply(t) shouldBe s
      }
    }

    "convert mixed case string to case object" in {
      forAll(values) { (s, t) =>
        AccessType.apply(t.toUpperCase()) shouldBe Some(s)
        AccessType.unsafeApply(t.toUpperCase()) shouldBe s
      }
    }

    "convert string value to None when undefined or empty" in {
      AccessType.apply("rubbish") shouldBe None
      AccessType.apply("") shouldBe None
    }

    "throw when string value is invalid" in {
      intercept[RuntimeException] {
        AccessType.unsafeApply("rubbish")
      }.getMessage() should include("Access Type")
    }

    "read from Json" in {
      forAll(values) { (s, t) =>
        testFromJson[AccessType](s""" "$t" """)(s)
      }
    }

    "read with error from Json" in {
      intercept[Exception] {
        testFromJson[AccessType](s"""123""")(AccessType.STANDARD)
      }.getMessage() should include("Cannot parse Access Type from '123'")
    }

    "write to Json" in {
      forAll(values) { (s, t) =>
        Json.toJson[AccessType](s) shouldBe JsString(t.toUpperCase())
      }
    }
  }
}
