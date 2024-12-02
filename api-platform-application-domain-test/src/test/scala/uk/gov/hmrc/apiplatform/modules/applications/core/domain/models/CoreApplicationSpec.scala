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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import scala.util.Random

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.{Access, AccessSpec, OverrideFlag}
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class CoreApplicationSpec extends BaseJsonFormattersSpec with CoreApplicationFixtures {
  import CoreApplicationSpec._

  "CoreApplication" should {
    "convert to json" in {
      Json.toJson[CoreApplication](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[CoreApplication](jsonText)(example)
    }

    val modifyPrivAccess: (Access) => Access                  = a =>
      a match {
        case p: Access.Privileged => p.copy(scopes = p.scopes + "NewScope")
        case other                => other
      }
    val modifyStdAccess: (Access.Standard) => Access.Standard = a => a.copy(overrides = Set(OverrideFlag.PersistLogin))

    "modify access" in {
      val app    = example.copy(access = privilegedAccess)
      val newApp = app.modifyAccess(modifyPrivAccess)

      newApp.access.asInstanceOf[Access.Privileged].scopes shouldBe privilegedAccess.scopes + "NewScope"
    }

    "modify standard access does nothing for privileged access" in {
      val app    = example.copy(access = privilegedAccess)
      val newApp = app.modifyStdAccess(modifyStdAccess)

      newApp shouldBe app
    }

    "modify standard access changes app with standard access" in {
      val app    = example.copy(access = standardAccess)
      val newApp = app.modifyStdAccess(modifyStdAccess)

      newApp.access.asInstanceOf[Access.Standard].overrides shouldBe Set(OverrideFlag.PersistLogin)
    }

    "orders correctly" in {
      val apps =
        List("a", "b", "c", "d", "e", "f", "g").map(ApplicationName(_)).map(n => standardCoreApp.withId(ApplicationId.random).copy(name = n))

      val rnd = Random.shuffle(apps)
      rnd should not be apps
      rnd.sorted shouldBe apps
    }
  }
}

object CoreApplicationSpec extends FixedClock {
  val id       = ApplicationId.random
  val clientId = ClientId.random

  val example = CoreApplication(
    id,
    clientId,
    gatewayId = "",
    name = ApplicationName("App"),
    deployedTo = Environment.PRODUCTION,
    description = None,
    createdOn = instant,
    lastAccess = None,
    grantLength = GrantLength.EIGHTEEN_MONTHS,
    lastAccessTokenUsage = None,
    access = Access.Standard(),
    state = ApplicationStateSpec.example,
    rateLimitTier = RateLimitTier.BRONZE,
    checkInformation = None,
    blocked = false,
    ipAllowlist = IpAllowlist(false, Set.empty),
    allowAutoDelete = false,
    deleteRestriction = DeleteRestriction.NoRestriction,
    lastActionActor = ActorType.UNKNOWN
  )

  val jsonText =
    s"""{"id":"$id","clientId":"$clientId","gatewayId":"","name":"App","deployedTo":"PRODUCTION","createdOn":"$nowAsText","grantLength":"P547D","access":${AccessSpec.emptyStandard},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"ipAllowlist":{"required":false,"allowlist":[]},"allowAutoDelete":false,"deleteRestriction":{"deleteRestrictionType":"NO_RESTRICTION"},"lastActionActor":"UNKNOWN"}"""

}
