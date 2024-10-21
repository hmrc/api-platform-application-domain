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
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actors

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.SellResellOrDistribute

class ChangeApplicationSellResellOrDistributeSpec extends ApplicationCommandBaseSpec {

  val sellResellOrDistribute = SellResellOrDistribute("Yes")

  "ChangeApplicationSellResellOrDistribute" should {
    val cmd = ApplicationCommands.ChangeApplicationSellResellOrDistribute(Actors.AppCollaborator(anActorEmail), aTimestamp, sellResellOrDistribute)

    "write to json (as a command)" in {

      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "actor"                  -> Json.obj(
          "email" -> anActorEmail.text
        ),
        "timestamp"              -> s"$nowAsText",
        "sellResellOrDistribute" -> "Yes",
        "updateType"             -> "changeApplicationSellResellOrDistribute"
      )
    }

    "read from json" in {
      val jsonText =
        s"""{"actor":{"email":"${anActorEmail.text}"},"timestamp":"$nowAsText","sellResellOrDistribute":"Yes","updateType":"changeApplicationSellResellOrDistribute"}"""

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
