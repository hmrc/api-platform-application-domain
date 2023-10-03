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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import play.api.libs.json.Json

import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actors
import uk.gov.hmrc.apiplatform.modules.applications.domain.models.CidrBlock

class ChangeIpAllowlistSpec extends ApplicationCommandBaseSpec {
  val updateType = "changeIpAllowlist"

  "ChangeIpAllowlist" should {
    val cmd = ApplicationCommands.ChangeIpAllowlist(Actors.AppCollaborator(anActorEmail), aTimestamp, false, List(CidrBlock("1.0.0.0/24")), List(CidrBlock("1.0.0.0/24"), CidrBlock("10.0.0.0/24")))

    "write to json (as a command)" in {
      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "actor"          -> Json.obj(
          "email"     -> "bob@example.com",
          "actorType" -> "COLLABORATOR"
        ),
        "timestamp"      -> s"$nowAsText",
        "required"       -> false,
        "oldIpAllowlist" -> List("1.0.0.0/24"),
        "newIpAllowlist" -> List("1.0.0.0/24", "10.0.0.0/24"),
        "updateType"     -> s"$updateType"
      )
    }

    "read from json" in {
      val jsonText =
        s"""{"actor":{"email":"${anActorEmail.text}","actorType":"COLLABORATOR"},"timestamp":"$nowAsText","required": false, "oldIpAllowlist":["1.0.0.0/24"],"newIpAllowlist":["1.0.0.0/24", "10.0.0.0/24"],"updateType":"$updateType"}"""

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
