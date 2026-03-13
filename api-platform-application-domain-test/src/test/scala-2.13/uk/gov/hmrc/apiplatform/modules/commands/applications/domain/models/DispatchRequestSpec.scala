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

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actors

class DispatchRequestSpec extends ApplicationCommandBaseSpec {

  "DispatchRequest" should {
    val cmd = ApplicationCommands.AddCollaborator(Actors.AppCollaborator(anActorEmail), aCollaborator, aTimestamp)

    "write to json" in {
      val request = DispatchRequest(cmd, Set(anActorEmail))

      Json.toJson[DispatchRequest](request) shouldBe Json.obj(
        "command"                       -> Json.obj(
          "actor"        -> Json.obj(
            "email"     -> "bob@example.com",
            "actorType" -> "COLLABORATOR"
          ),
          "collaborator" -> Json.obj(
            "emailAddress" -> "alice@example.com",
            "role"         -> "DEVELOPER",
            "userId"       -> s"$aUserId"
          ),
          "timestamp"    -> s"$nowAsText",
          "updateType"   -> "addCollaborator"
        ),
        "verifiedCollaboratorsToNotify" -> Json.arr(JsString(anActorEmail.text))
      )
    }

    "read from json" in {
      val jsonText =
        s"""{ "command": {"actor":{"email":"bob@example.com","actorType":"COLLABORATOR"},"collaborator":{"emailAddress":"alice@example.com","role":"DEVELOPER","userId":"$aUserId"},"timestamp":"$nowAsText","updateType":"addCollaborator"}, "verifiedCollaboratorsToNotify": ["${anActorEmail.text}"] }"""

      Json.parse(jsonText).as[DispatchRequest] shouldBe DispatchRequest(cmd, Set(anActorEmail))
    }

    "read from json when only a command is present" in {
      val jsonText =
        s"""{ "actor":{"email":"bob@example.com","actorType":"COLLABORATOR"},"collaborator":{"emailAddress":"alice@example.com","role":"DEVELOPER","userId":"$aUserId"},"timestamp":"$nowAsText","updateType":"addCollaborator"}"""

      Json.parse(jsonText).as[DispatchRequest] shouldBe DispatchRequest(cmd, Set.empty)
    }
  }
}
