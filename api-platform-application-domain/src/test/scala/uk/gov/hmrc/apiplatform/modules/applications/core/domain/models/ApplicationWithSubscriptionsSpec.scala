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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationWithSubscriptionsSpec extends BaseJsonFormattersSpec {
  import ApplicationWithSubscriptionsSpec._

  "ApplicationWithSubscriptions" should {
    "convert to json with no subs" in {
      import NoSubs._
      Json.toJson[ApplicationWithSubscriptions](example) shouldBe Json.parse(jsonText)
    }

    "read from json with no subs" in {
      import NoSubs._
      testFromJson[ApplicationWithSubscriptions](jsonText)(example)
    }

    "read from old json" in {
      import NoSubs._
      val oldJson =
        s"""{"id":"${example.coreApp.id}","clientId":"${example.coreApp.clientId}","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.Admin.jsonText}],"createdOn":"$nowAsText","grantLength":"P547D","access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"serverToken":"123","trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"allowAutoDelete":false,"subscriptions":[]}"""

      testFromJson[ApplicationWithSubscriptions](oldJson)(example)
    }

    "convert to json with subs" in {
      import TwoSubs._
      Json.toJson[ApplicationWithSubscriptions](example) shouldBe Json.parse(jsonText)
    }

    "read from json with subs" in {
      import TwoSubs._
      testFromJson[ApplicationWithSubscriptions](jsonText)(example)
    }
  }
}

object ApplicationWithSubscriptionsSpec extends ApiIdentifierFixture with FixedClock {

  object NoSubs {

    val example = ApplicationWithSubscriptions(
      coreApp = CoreApplicationSpec.example,
      collaborators = Set(CollaboratorSpec.Admin.example),
      subscriptions = Set()
    )

    val jsonText =
      s"""{"coreApp":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}],"subscriptions":[]}"""
  }

  object TwoSubs extends ApiContextFixture with ApiVersionNbrFixture {

    val example = ApplicationWithSubscriptions(
      coreApp = CoreApplicationSpec.example,
      collaborators = Set(CollaboratorSpec.Admin.example),
      subscriptions = Set(apiIdentifierOne, apiIdentifierTwo)
    )

    val jsonText =
      s"""{"coreApp":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}],"subscriptions":[{"context":"$apiContextOne","version":"$apiVersionNbrOne"},{"context":"$apiContextOne","version":"$apiVersionNbrOnePointOne"}]}"""
  }
}
