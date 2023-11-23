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

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class AccessSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "Access" should {
    "correctly identify the AccessType" in {
      val values = Table(
        ("Access", "Expected Type"),
        (Access.Standard(), AccessType.STANDARD),
        (Access.Privileged(), AccessType.PRIVILEGED),
        (Access.Ropc(), AccessType.ROPC)
      )

      forAll(values) {
        case (access, accessType) => access.accessType shouldBe accessType
      }
    }

    val emptyStandard    = """{ "redirectUris":[],"overrides":[],"accessType":"STANDARD" }"""
    val emptyPriviledged = """{"scopes":[],"accessType":"PRIVILEGED"}"""
    val emptyRopc        = """{"scopes":[],"accessType":"ROPC"}"""

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
  }
}
