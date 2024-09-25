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
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationWithCollaboratorsSpec extends BaseJsonFormattersSpec {
  import ApplicationWithCollaboratorsSpec._
  import CollaboratorSpec.{Admin, Dev}

  "ApplicationWithCollaborators" should {
    "convert to json" in {
      Json.toJson[ApplicationWithCollaborators](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[ApplicationWithCollaborators](jsonText)(example)
    }

    "read from old json" in {
      val oldJson =
        s"""{"id":"${example.coreApp.id}","clientId":"${example.coreApp.clientId}","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.Admin.jsonText}],"createdOn":"$nowAsText","grantLength":"P547D","access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"moreApplication":{"allowAutoDelete":false,"lastActionActor":"UNKNOWN"}}"""

      testFromJson[ApplicationWithCollaborators](oldJson)(example)
    }

    "return the admins" in {
      val app = example.copy(collaborators = Set(Admin.example, Dev.example))
      app.admins shouldBe Set(Admin.example)
    }

    "identify a collaborator" in {
      val app = example.copy(collaborators = Set(Dev.example, Admin.example))
      app.isCollaborator(Dev.example.userId) shouldBe true
      app.roleFor(Dev.example.userId).value shouldBe Collaborator.Roles.DEVELOPER
    }
  }
}

object ApplicationWithCollaboratorsSpec extends FixedClock {

  val example = ApplicationWithCollaborators(
    coreApp = CoreApplicationSpec.example,
    collaborators = Set(CollaboratorSpec.Admin.example)
  )

  val jsonText =
    s"""{"coreApp":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}]}"""
}
