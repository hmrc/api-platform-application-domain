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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.RateLimitTier

class ChangeRateLimitTierSpec extends ApplicationCommandBaseSpec {
  val aRateLimitTier = RateLimitTier.Platinum
  val updateType     = "changeRateLimitTier"

  "ChangeRateLimitTier" should {
    val cmd = ApplicationCommands.ChangeRateLimitTier(aGatekeeperUser, aTimestamp, aRateLimitTier)

    "write to json (as a command)" in {
      /*
       * ChangeRateLimitTier should write to json (as a command) -
       * {"gatekeeperUser":"Bob in SDST","timestamp":"2020-01-02T03:04:05.006Z","rateLimitTier":"PLATINUM","updateType":"changeRateLimitTier"} was not equal to
       * {"gatekeeperUser":"Bob in SDST","timestamp":"2020-01-02T03:04:05.006Z","rateLimitTier":"Platinum","updateType":"changeRateLimitTier"}

       */
      Json.toJson[ApplicationCommand](cmd) shouldBe Json.obj(
        "gatekeeperUser" -> s"${aGatekeeperUser}",
        "timestamp"      -> s"$nowAsText",
        "rateLimitTier"  -> s"${aRateLimitTier.toString.toUpperCase}",
        "updateType"     -> s"$updateType"
      )
    }

    "read from json" in {
      val jsonText =
        s""" {"gatekeeperUser":"${aGatekeeperUser}","timestamp":"$nowAsText","rateLimitTier":"$aRateLimitTier","updateType":"$updateType"} """

      Json.parse(jsonText).as[ApplicationCommand] shouldBe cmd
    }
  }
}
