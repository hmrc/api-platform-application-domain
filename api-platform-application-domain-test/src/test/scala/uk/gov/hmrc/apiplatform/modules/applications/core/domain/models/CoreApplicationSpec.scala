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

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.{Access, AccessSpec}
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class CoreApplicationSpec extends BaseJsonFormattersSpec {
  import CoreApplicationSpec._

  "CoreApplication" should {
    "convert to json" in {
      Json.toJson[CoreApplication](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[CoreApplication](jsonText)(example)
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
    allowAutoDelete = false
  )

  val jsonText =
    s"""{"id":"$id","clientId":"$clientId","gatewayId":"","name":"App","deployedTo":"PRODUCTION","createdOn":"$nowAsText","grantLength":"P547D","access":${AccessSpec.emptyStandard},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"ipAllowlist":{"required":false,"allowlist":[]},"allowAutoDelete":false}"""

}
