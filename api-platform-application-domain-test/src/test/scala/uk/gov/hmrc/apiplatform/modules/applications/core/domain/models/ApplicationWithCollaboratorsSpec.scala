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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationWithCollaboratorsSpec extends BaseJsonFormattersSpec with ApplicationWithCollaboratorsFixtures {
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
        s"""{"id":"${example.details.id}","clientId":"${example.details.clientId}","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.Admin.jsonText}],"createdOn":"$nowAsText","grantLength":"P547D","access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"moreApplication":{"allowAutoDelete":false,"lastActionActor":"UNKNOWN"}}"""

      testFromJson[ApplicationWithCollaborators](oldJson)(example)
    }

    "read as GK" in {
      val applicationResponseForEmail =
        s"""
           |  [{
           |    "id": "$applicationIdOne",
           |    "clientId": "clientid1",
           |    "gatewayId": "gatewayId1",
           |    "name": "Automated Test Application",
           |    "description": "application for test",
           |    "deployedTo": "PRODUCTION",
           |   "collaborators": [
           |    {
           |      "emailAddress": "$emailOne",
           |      "userId": "$userIdOne",
           |     "role": "ADMINISTRATOR"
           |    },
           |    {
           |      "emailAddress": "$emailTwo",
           |      "userId": "$userIdTwo",
           |     "role": "DEVELOPER"
           |    }
           |    ],
           |    "createdOn": 1458832690624,
           |    "lastAccess": 1458832690624,
           |    "access": {
           |      "redirectUris": [],
           |      "overrides": [],
           |      "accessType": "STANDARD"
           |    },
           |    "rateLimitTier": "BRONZE",
           |    "blocked": false,
           |    "trusted": false,
           |    "state": {
           |      "name": "PRODUCTION",
           |      "requestedByEmailAddress": "$emailOne",
           |      "verificationCode": "pRoPW05BMTQ_HqzTTR0Ent10py9gvstX34_a3dxx4V8",
           |      "updatedOn": 1459868573962
           |    },
           |    "ipAllowlist" : {
           |        "required" : false,
           |        "allowlist" : []
           |    },
           |    "grantLength": 547,
           |    "redirectUris": [],
           |    "moreApplication": {
           |        "allowAutoDelete": true,
           |        "lastActionActor": "UNKNOWN"
           |      }
           |  }]
    """.stripMargin

      val js  = Json.parse(applicationResponseForEmail)
      val res = js.validate[List[ApplicationWithCollaborators]]
      println(res.asEither.left)
      res.asOpt.value
    }

    "read as if from APM" in {
      val applicationForDeveloperResponse: String =
        """[
          |  {
          |    "id": "b42c4a8f-3df3-451f-92ea-114ff039110e",
          |    "clientId": "qDxLu6_zZVGurMX7NA7g2Wd5T5Ia",
          |    "gatewayId": "12345",
          |    "name": "application for test",
          |    "deployedTo": "PRODUCTION",
          |    "collaborators": [
          |      {
          |        "userId": "8e6657be-3b86-42b7-bcdf-855bee3bf941",
          |        "emailAddress": "a@b.com",
          |        "role": "ADMINISTRATOR"
          |      }
          |    ],
          |    "createdOn": 1678792287460,
          |    "lastAccess": 1678792287460,
          |    "grantLength": 547,
          |    "redirectUris": [
          |      "http://red1",
          |      "http://red2"
          |    ],
          |    "access": {
          |      "redirectUris": [
          |        "http://isobel.name",
          |        "http://meghan.biz"
          |      ],
          |      "overrides": [],
          |      "accessType": "STANDARD"
          |    },
          |    "state": {
          |      "name": "PRODUCTION",
          |      "updatedOn": 1678793142888
          |    },
          |    "rateLimitTier": "BRONZE",
          |    "blocked": false,
          |    "trusted": false,
          |    "serverToken": "2faa09169cf8f464ce13b80a14718b15",
          |    "subscriptions": [],
          |    "ipAllowlist": {
          |      "required": false,
          |      "allowlist": []
          |    }
          |  }
          |]""".stripMargin

      val js  = Json.parse(applicationForDeveloperResponse)
      val res = js.validate[List[ApplicationWithCollaborators]]
      println(res.asEither.left)
      res.asOpt.value
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

    val modifyPrivAccess: (Access) => Access                  = a =>
      a match {
        case p: Access.Privileged => p.copy(scopes = p.scopes + "NewScope")
        case other                => other
      }
    val modifyStdAccess: (Access.Standard) => Access.Standard = a => a.copy(overrides = Set(OverrideFlag.PersistLogin))
    val changeDescription: CoreApplication => CoreApplication = a => a.copy(description = Some("a new description"))

    "support modify to change the core" in {
      val newApp = example.modify(changeDescription)

      newApp.details.description shouldBe Some("a new description")
    }

    "support withAccess to replace it" in {
      val newApp = example.withAccess(privilegedAccess)

      newApp.details.access.isPriviledged shouldBe true
    }

    "support modifyAccess" in {
      val app    = example.withAccess(privilegedAccess)
      val newApp = app.modifyAccess(modifyPrivAccess)

      newApp.access.asInstanceOf[Access.Privileged].scopes shouldBe privilegedAccess.scopes + "NewScope"
    }

    "support modifyStdAccess to do nothing for privileged access" in {
      val app    = example.withAccess(privilegedAccess)
      val newApp = app.modifyStdAccess(modifyStdAccess)

      newApp shouldBe app
    }

    "support modifyStdAccess to make changes to app with standard access" in {
      val app    = example.withAccess(standardAccess)
      val newApp = app.modifyStdAccess(modifyStdAccess)

      newApp.access.asInstanceOf[Access.Standard].overrides shouldBe Set(OverrideFlag.PersistLogin)
    }

    "support withState to replace it" in {
      val newApp = example.withState(ApplicationStateData.preProduction)

      newApp.details.state.isInPreProductionOrProduction shouldBe true
    }

    val modifyState: ApplicationState => ApplicationState = a => a.copy(requestedByName = Some("Bob"))

    "support modifyState to change it" in {
      val newApp = example.modifyState(modifyState)

      newApp.details.state.requestedByName shouldBe Some("Bob")
    }
  }
}

object ApplicationWithCollaboratorsSpec extends FixedClock {

  val example = ApplicationWithCollaborators(
    details = CoreApplicationSpec.example,
    collaborators = Set(CollaboratorSpec.Admin.example)
  )

  val jsonText =
    s"""{"details":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}]}"""
}
