/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class DeleteTypeRestrictionSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  val values =
    Table(
      ("Type", "text"),
      (DeleteRestrictionType.NO_RESTRICTION, "no_restriction"),
      (DeleteRestrictionType.DO_NOT_DELETE, "do_not_delete")
    )

  "convert lower case string to case object" in {
    forAll(values) { (s, t) =>
      DeleteRestrictionType.apply(t) shouldBe Some(s)
      DeleteRestrictionType.unsafeApply(t) shouldBe s
    }
  }

  "convert mixed case string to case object" in {
    forAll(values) { (s, t) =>
      DeleteRestrictionType.apply(t.toUpperCase()) shouldBe Some(s)
      DeleteRestrictionType.unsafeApply(t.toUpperCase()) shouldBe s
    }
  }

  "convert string value to None when undefined or empty" in {
    DeleteRestrictionType.apply("rubbish") shouldBe None
    DeleteRestrictionType.apply("") shouldBe None
  }

  "throw when string value is invalid" in {
    intercept[RuntimeException] {
      DeleteRestrictionType.unsafeApply("rubbish")
    }.getMessage() should include("Delete Restriction Type")
  }

  "read from Json" in {
    forAll(values) { (s, t) =>
      testFromJson[DeleteRestrictionType](s""" "$t" """)(s)
    }
  }

  "read with error from Json" in {
    intercept[Exception] {
      testFromJson[DeleteRestrictionType](s"""123""")(DeleteRestrictionType.NO_RESTRICTION)
    }.getMessage() should include("Cannot parse Delete Restriction Type from '123'")
  }

  "write to Json" in {
    forAll(values) { (s, t) =>
      Json.toJson[DeleteRestrictionType](s) shouldBe JsString(t.toUpperCase())
    }
  }
}
