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

class GrantApplicationApprovalRequestSpec extends ApplicationCommandBaseSpec {

  val warningString = Some("a warning for you")
  val escalatedTo   = Some("top dog")

  "GrantApplicationApprovalRequest" should {
    val cmd = ApplicationCommands.GrantApplicationApprovalRequest(aGatekeeperUser, aTimestamp, warningString, escalatedTo)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "gatekeeperUser" -> s"${aGatekeeperUser}",
        "timestamp"      -> s"$nowAsText",
        "warnings"       -> s"${warningString.get}",
        "escalatedTo"    -> s"${escalatedTo.get}",
        "updateType"     -> "grantApplicationApprovalRequest"
      )
    }

    "read from json" in {
      val jsonTextWitheEscalatedTo =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","warnings":"${warningString.get}", "escalatedTo":"${escalatedTo.get}", "updateType":"grantApplicationApprovalRequest"} """

      Json.parse(jsonTextWitheEscalatedTo).as[ApplicationCommand] shouldBe cmd

      val jsonText =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","warnings":"${warningString.get}","updateType":"grantApplicationApprovalRequest"} """

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd.copy(escalatedTo = None)

      val jsonWithoutWarningsorEscalatedToText =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","updateType":"grantApplicationApprovalRequest"} """

      Json.parse(jsonWithoutWarningsorEscalatedToText).as[ApplicationCommand] shouldBe cmd.copy(escalatedTo = None, warnings = None)
    }
  }
}
