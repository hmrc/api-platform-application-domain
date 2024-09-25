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

class GrantTermsOfUseApprovalSpec extends ApplicationCommandBaseSpec {

  val escalatedTo = Some("top dog")

  "GrantTermsOfUseApproval" should {
    val cmd = ApplicationCommands.GrantTermsOfUseApproval(aGatekeeperUser, aTimestamp, reasons, escalatedTo)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "gatekeeperUser" -> s"${aGatekeeperUser}",
        "timestamp"      -> s"$nowAsText",
        "reasons"        -> s"$reasons",
        "escalatedTo"    -> s"${escalatedTo.get}",
        "updateType"     -> "grantTermsOfUseApproval"
      )
    }

    "read from json" in {
      val jsonTextWithEscalatedTo =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","reasons":"$reasons","escalatedTo":"${escalatedTo.get}","updateType":"grantTermsOfUseApproval"} """

      Json.parse(jsonTextWithEscalatedTo).as[ApplicationCommand] shouldBe cmd

      val jsonTextWithOutEscalatedTo =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","reasons":"$reasons","updateType":"grantTermsOfUseApproval"} """

      Json.parse(jsonTextWithOutEscalatedTo).as[ApplicationCommand] shouldBe cmd.copy(escalatedTo = None)
    }
  }
}
