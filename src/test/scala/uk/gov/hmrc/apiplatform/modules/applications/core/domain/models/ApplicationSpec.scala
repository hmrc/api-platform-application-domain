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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import play.api.libs.json.Json

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId, Environment}
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

class ApplicationSpec extends BaseJsonFormattersSpec {
  import ApplicationSpec._

  "Application" should {
    "convert to json" in {
      Json.toJson[Application](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[Application](jsonText)(example)
    }
  }
}

object ApplicationSpec extends FixedClock {
  val id       = ApplicationId.random
  val clientId = ClientId.random

  val example = Application(
    id,
    clientId,
    gatewayId = "",
    name = "App",
    deployedTo = Environment.PRODUCTION.toString(),
    description = None,
    collaborators = Set(CollaboratorSpec.exampleAdmin),
    createdOn = now(),
    lastAccess = None,
    grantLength = GrantLength.EIGHTEEN_MONTHS.days,
    lastAccessTokenUsage = None,
    redirectUris = List.empty,
    termsAndConditionsUrl = None,
    privacyPolicyUrl = None,
    access = Access.Standard(),
    state = ApplicationStateSpec.example,
    rateLimitTier = RateLimitTier.BRONZE,
    checkInformation = None,
    blocked = false,
    trusted = false,
    ipAllowlist = IpAllowlist(false, Set.empty),
    moreApplication = MoreApplication(false)
  )

  val jsonText =
    s"""{"id":"$id","clientId":"$clientId","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.jsonTextForAdmin}],"createdOn":"$nowAsText","grantLength":547,"redirectUris":[],"access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"moreApplication":{"allowAutoDelete":false}}"""
}
