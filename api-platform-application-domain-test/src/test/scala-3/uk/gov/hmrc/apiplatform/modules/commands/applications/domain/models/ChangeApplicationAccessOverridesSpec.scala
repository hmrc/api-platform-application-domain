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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import play.api.libs.json.Json

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.OverrideFlag

class ChangeApplicationAccessOverridesSpec extends ApplicationCommandBaseSpec {

  "ChangeApplicationAccessOverrides" should {
    val overrides: Set[OverrideFlag] = Set(OverrideFlag.OriginOverride("origin1"), OverrideFlag.SuppressIvForAgents(Set("scope01", "scope02")))
    val cmd                          = ApplicationCommands.ChangeApplicationAccessOverrides(aGatekeeperUser, overrides, aTimestamp)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "gatekeeperUser" -> s"${aGatekeeperUser}",
        "overrides"      -> overrides,
        "timestamp"      -> s"$nowAsText",
        "updateType"     -> "changeApplicationAccessOverrides"
      )
    }

    "read from json" in {
      val jsonText =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","overrides":[{"overrideType":"ORIGIN_OVERRIDE","origin":"origin1"},{"overrideType":"SUPPRESS_IV_FOR_AGENTS","scopes":["scope01", "scope02"]}],"timestamp":"$nowAsText","updateType":"changeApplicationAccessOverrides"} """

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
