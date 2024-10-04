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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationWithSubscriptionFieldsSpec extends BaseJsonFormattersSpec with ApplicationWithCollaboratorsFixtures with ApiIdentifierFixtures {
  import ApplicationWithSubscriptionFieldsSpec._

  "ApplicationWithSubscriptionFields" should {
    "convert to json" in {
      Json.toJson[ApplicationWithSubscriptionFields](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[ApplicationWithSubscriptionFields](jsonText)(example)
    }

    "read from old json" in {
      val oldJson =
        s"""{"application":{"id":"${example.details.id}","clientId":"${example.details.clientId}","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.Admin.jsonText}],"createdOn":"$nowAsText","grantLength":"P547D","access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"moreApplication":{"allowAutoDelete":false,"lastActionActor":"UNKNOWN"}}, "subscriptions":[{"context":"$apiContextOne","versionNbr":"$apiVersionNbrOne"}],"subscriptionFieldValues": {}}"""

      testFromJson[ApplicationWithSubscriptionFields](oldJson)(example.copy(fieldValues = Map.empty))
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

object ApplicationWithSubscriptionFieldsSpec extends ApiIdentifierFixtures with FixedClock {

  val example = ApplicationWithSubscriptionFields(
    details = CoreApplicationSpec.example,
    collaborators = Set(CollaboratorSpec.Admin.example),
    subscriptions = Set(apiIdentifierOne),
    fieldValues = Map(apiIdentifierOne.context -> Map(apiIdentifierOne.versionNbr -> Map(FieldName("one") -> FieldValue("a"))))
  )

  val jsonText =
    s"""{"details":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}],"subscriptions":[{"context":"${apiIdentifierOne.context}","version":"${apiIdentifierOne.versionNbr}"}],"fieldValues":{"test/contextA":{"1.0":{"one":"a"}}}}"""
}
