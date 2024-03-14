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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.GrantLength

class ChangeGrantLengthSpec extends ApplicationCommandBaseSpec {
  val aGrantLength = GrantLength.SIX_MONTHS
  val updateType   = "changeGrantLength"

  "ChangeGrantLength" should {
    val cmd = ApplicationCommands.ChangeGrantLength(aGatekeeperUser, aTimestamp, aGrantLength)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "gatekeeperUser"    -> s"${aGatekeeperUser}",
        "timestamp"         -> s"$nowAsText",
        "grantLength" -> aGrantLength.duration.toDays,
        "updateType"        -> s"$updateType"
      )
    }

    "read from json where grant length is Int" in {
      val jsonText =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","grantLength":${aGrantLength.duration.toDays},"updateType":"$updateType"} """

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }

    "read from json where grant length is Duration" in {
      val jsonText =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","grantLength":{"length":180, "unit":"days"},"updateType":"$updateType"} """

      println(s"****** $jsonText")

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
