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
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.LoginRedirectUri

class UpdateLoginRedirectUrisSpec extends ApplicationCommandBaseSpec {
  val base  = "http://localhost:1001"
  val one   = LoginRedirectUri.unsafeApply(s"$base/a")
  val two   = LoginRedirectUri.unsafeApply(s"$base/b")
  val three = LoginRedirectUri.unsafeApply(s"$base/c")
  val four  = LoginRedirectUri.unsafeApply(s"$base/x")

  val oldUris = List(one, two, three)
  val newUris = List(one, two, four)

  "UpdateRedirectUris" should {
    val cmd = ApplicationCommands.UpdateLoginRedirectUris(Actors.AppCollaborator(anActorEmail), newUris, aTimestamp)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "actor"           -> Json.obj(
          "email"     -> "bob@example.com",
          "actorType" -> "COLLABORATOR"
        ),
        "newRedirectUris" -> Json.arr(one, two, four),
        "timestamp"       -> s"$nowAsText",
        "updateType"      -> "updateRedirectUris"
      )
    }

    "read from json" in {
      val jsonText =
        s""" {"actor":{"email":"bob@example.com","actorType":"COLLABORATOR"},"timestamp":"$nowAsText","newRedirectUris":["$base/a","$base/b","$base/x"],"updateType":"updateRedirectUris"} """

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
