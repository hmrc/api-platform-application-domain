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

class OverrideFlagSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {

  "OverrideFlag" should {
    "correctly identify the OverrideType" in {
      val values = Table(
        ("OverrideFlag", "Expected Type"),
        (OverrideFlag.PersistLogin, OverrideType.PERSIST_LOGIN_AFTER_GRANT),
        (OverrideFlag.GrantWithoutConsent(Set.empty), OverrideType.GRANT_WITHOUT_TAXPAYER_CONSENT),
        (OverrideFlag.SuppressIvForAgents(Set.empty), OverrideType.SUPPRESS_IV_FOR_AGENTS),
        (OverrideFlag.SuppressIvForIndividuals(Set.empty), OverrideType.SUPPRESS_IV_FOR_INDIVIDUALS),
        (OverrideFlag.SuppressIvForOrganisations(Set.empty), OverrideType.SUPPRESS_IV_FOR_ORGANISATIONS),
        (OverrideFlag.OriginOverride("an origin"), OverrideType.ORIGIN_OVERRIDE)
      )

      forAll(values) {
        case (overrideFlag, overrideType) => OverrideFlag.asOverrideType(overrideFlag) shouldBe overrideType
      }
    }

    val persistLogin         = """{"overrideType":"PERSIST_LOGIN_AFTER_GRANT"}"""
    val emptyGWC             = """{"scopes":[],"overrideType":"GRANT_WITHOUT_TAXPAYER_CONSENT"}"""
    val emptyAgents          = """{"scopes":[],"overrideType":"SUPPRESS_IV_FOR_AGENTS"}"""
    val emptyIndividuals     = """{"scopes":[],"overrideType":"SUPPRESS_IV_FOR_INDIVIDUALS"}"""
    val emptyOrganisations   = """{"scopes":[],"overrideType":"SUPPRESS_IV_FOR_ORGANISATIONS"}"""
    val simpleOriginOverride = """{"origin":"an origin","overrideType":"ORIGIN_OVERRIDE"}"""

    val values = Table(
      ("OverrideFlag", "Expected Json"),
      (OverrideFlag.PersistLogin, persistLogin),
      (OverrideFlag.GrantWithoutConsent(Set.empty), emptyGWC),
      (OverrideFlag.SuppressIvForAgents(Set.empty), emptyAgents),
      (OverrideFlag.SuppressIvForIndividuals(Set.empty), emptyIndividuals),
      (OverrideFlag.SuppressIvForOrganisations(Set.empty), emptyOrganisations),
      (OverrideFlag.OriginOverride("an origin"), simpleOriginOverride)
    )

    "correctly write the default OverrideFlag to Json" in {
      forAll(values) {
        (overrideFlag, expectedJson) => Json.toJson[OverrideFlag](overrideFlag) shouldBe Json.parse(expectedJson)
      }
    }

    "correctly read the default OverrideFlag to Json" in {
      forAll(values) {
        (overrideFlag, expectedJson) => Json.parse(expectedJson).as[OverrideFlag] shouldBe overrideFlag
      }
    }

    "toString" in {
      val values = Table(
        ("OverrideFlag", "ExpectedString"),
        (OverrideFlag.PersistLogin, "PersistLogin"),
        (OverrideFlag.GrantWithoutConsent(Set("scope01")), "GrantWithoutConsent(scope01)"),
        (OverrideFlag.SuppressIvForAgents(Set("scope01", "scope02")), "SuppressIvForAgents(scope01,scope02)"),
        (OverrideFlag.SuppressIvForIndividuals(Set("scope01", "scope02", "scope03")), "SuppressIvForIndividuals(scope01,scope02,scope03)"),
        (OverrideFlag.SuppressIvForOrganisations(Set.empty), "SuppressIvForOrganisations()"),
        (OverrideFlag.OriginOverride("origin01"), "OriginOverride(origin01)")
      )

      forAll(values) {
        case (overrideFlag, expectedString) => overrideFlag.toString shouldBe expectedString
      }
    }
  }
}
