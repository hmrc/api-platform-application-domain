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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

class ChangeRedirectUriSpec extends ApplicationCommandBaseSpec {

  "AddRedirectUri" should {
    val cmd = ApplicationCommands.ChangeRedirectUri(Actors.AppCollaborator(anActorEmail), redirectUriToChange.value, redirectUri.value, aTimestamp)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "actor"                -> Json.obj(
          "email"     -> "bob@example.com",
          "actorType" -> "COLLABORATOR"
        ),
        "redirectUriToReplace" -> s"${redirectUriToChange.value.uri}",
        "redirectUri"          -> s"${redirectUri.value.uri}",
        "timestamp"            -> s"$nowAsText",
        "updateType"           -> "changeRedirectUri"
      )
    }

    "read from json" in {
      val jsonText =
        s""" {"actor":{"email":"bob@example.com","actorType":"COLLABORATOR"},"redirectUriToReplace":"${redirectUriToChange.value.uri}","redirectUri":"${redirectUri.value.uri}","timestamp":"$nowAsText","updateType":"changeRedirectUri"} """

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
