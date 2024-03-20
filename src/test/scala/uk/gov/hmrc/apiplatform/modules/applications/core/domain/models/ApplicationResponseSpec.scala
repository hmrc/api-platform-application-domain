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
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId, Environment}
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationResponseSpec extends BaseJsonFormattersSpec {
  import ApplicationResponseSpec._

  "Application" should {
    "convert to json" in {
      Json.toJson[ApplicationResponse](example) shouldBe Json.parse(jsonTextWithGrantLengthInt)
    }

    "read from json with Grant Length as Duration" in {
      testFromJson[ApplicationResponse](jsonTextWithGrantLengthDuration)(example)
    }

    "read from json with Grant Length as Int" in {
      testFromJson[ApplicationResponse](jsonTextWithGrantLengthInt)(example)
    }

    "return the admins" in {
      val admin = CollaboratorSpec.exampleAdmin
      val app   = example.copy(collaborators = Set(admin, CollaboratorSpec.exampleDeveloper))
      app.admins shouldBe Set(admin)
    }

    "return the developers" in {
      val developer = CollaboratorSpec.exampleDeveloper
      val app       = example.copy(collaborators = Set(developer, CollaboratorSpec.exampleAdmin))
      app.developers shouldBe Set(developer)
    }
  }
}

object ApplicationResponseSpec extends FixedClock {
  val id       = ApplicationId.random
  val clientId = ClientId.random

  val example = ApplicationResponse(
    id,
    clientId,
    gatewayId = "",
    name = "App",
    deployedTo = Environment.PRODUCTION,
    description = None,
    collaborators = Set(CollaboratorSpec.exampleAdmin),
    createdOn = instant,
    lastAccess = None,
    grantLength = GrantLength.EIGHTEEN_MONTHS,
    lastAccessTokenUsage = None,
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

  val jsonTextWithGrantLengthDuration =
    s"""{"id":"$id","clientId":"$clientId","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.jsonTextForAdmin}],"createdOn":"$nowAsText","grantLength":{"amount":547,"unit":"DAYS"},"access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"moreApplication":{"allowAutoDelete":false}}"""

  val jsonTextWithGrantLengthInt =
    s"""{"id":"$id","clientId":"$clientId","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.jsonTextForAdmin}],"createdOn":"$nowAsText","grantLength":547,"access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"moreApplication":{"allowAutoDelete":false}}"""

}
