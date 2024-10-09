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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationWithSubscriptionsSpec extends BaseJsonFormattersSpec with ApplicationWithCollaboratorsFixtures {
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
        s"""{"id":"${example.details.id}","clientId":"${example.details.clientId}","gatewayId":"","name":"App","deployedTo":"PRODUCTION","collaborators":[${CollaboratorSpec.Admin.jsonText}],"createdOn":"$nowAsText","grantLength":"P547D","access":{"redirectUris":[],"overrides":[],"accessType":"STANDARD"},"state":${ApplicationStateSpec.jsonText},"rateLimitTier":"BRONZE","blocked":false,"serverToken":"123","trusted":false,"ipAllowlist":{"required":false,"allowlist":[]},"allowAutoDelete":false,"subscriptions":[]}"""

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

    val modifyPrivAccess: (Access) => Access                  = a =>
      a match {
        case p: Access.Privileged => p.copy(scopes = p.scopes + "NewScope")
        case other                => other
      }
    val modifyStdAccess: (Access.Standard) => Access.Standard = a => a.copy(overrides = Set(OverrideFlag.PersistLogin))
    val changeDescription: CoreApplication => CoreApplication = a => a.copy(description = Some("a new description"))

    "support modify to change the core" in {
      val newApp = NoSubs.example.modify(changeDescription)

      newApp.details.description shouldBe Some("a new description")
    }

    "uplift to with subscription fields" in {
      TwoSubs.example.withFieldValues(ApplicationWithSubscriptionFieldsData.someFieldValues) shouldBe ApplicationWithSubscriptionFields(
        TwoSubs.example.details,
        TwoSubs.example.collaborators,
        TwoSubs.example.subscriptions,
        ApplicationWithSubscriptionFieldsData.someFieldValues
      )
    }

    "downgrades to with collaborators" in {
      TwoSubs.example.asAppWithCollaborators shouldBe ApplicationWithCollaborators(TwoSubs.example.details, TwoSubs.example.collaborators)
    }

    "support withAccess to replace it" in {
      val newApp = NoSubs.example.withAccess(privilegedAccess)

      newApp.details.access.isPriviledged shouldBe true
    }

    "support modifyAccess" in {
      val app    = NoSubs.example.withAccess(privilegedAccess)
      val newApp = app.modifyAccess(modifyPrivAccess)

      newApp.access.asInstanceOf[Access.Privileged].scopes shouldBe privilegedAccess.scopes + "NewScope"
    }

    "support modifyStdAccess to do nothing for privileged access" in {
      val app    = NoSubs.example.withAccess(privilegedAccess)
      val newApp = app.modifyStdAccess(modifyStdAccess)

      newApp shouldBe app
    }

    "support modifyStdAccess to make changes to app with standard access" in {
      val app    = NoSubs.example.withAccess(standardAccess)
      val newApp = app.modifyStdAccess(modifyStdAccess)

      newApp.access.asInstanceOf[Access.Standard].overrides shouldBe Set(OverrideFlag.PersistLogin)
    }

    "support withState to replace it" in {
      val newApp = NoSubs.example.withState(ApplicationStateData.preProduction)

      newApp.details.isInPreProductionOrProduction shouldBe true
    }

    val modifyState: ApplicationState => ApplicationState = a => a.copy(requestedByName = Some("Bob"))

    "support modifyState to change it" in {
      val newApp = NoSubs.example.modifyState(modifyState)

      newApp.details.state.requestedByName shouldBe Some("Bob")
    }

    "orders correctly" in {
      val apps =
        List("a", "b", "c", "d", "e", "f", "g").map(ApplicationName(_)).map(n => standardApp.withId(ApplicationId.random).modify(_.copy(name = n)).withSubscriptions(Set.empty))

      val rnd = Random.shuffle(apps)
      rnd should not be apps
      rnd.sorted shouldBe apps
    }
  }
}

object ApplicationWithSubscriptionsSpec extends ApiIdentifierFixtures with FixedClock {

  object NoSubs {

    val example = ApplicationWithSubscriptions(
      details = CoreApplicationSpec.example,
      collaborators = Set(CollaboratorSpec.Admin.example),
      subscriptions = Set()
    )

    val jsonText =
      s"""{"details":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}],"subscriptions":[]}"""
  }

  object TwoSubs extends ApiContextFixtures with ApiVersionNbrFixtures {

    val example = ApplicationWithSubscriptions(
      details = CoreApplicationSpec.example,
      collaborators = Set(CollaboratorSpec.Admin.example),
      subscriptions = Set(apiIdentifierOne, apiIdentifierTwo)
    )

    val jsonText =
      s"""{"details":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}],"subscriptions":[{"context":"$apiContextOne","version":"$apiVersionNbrOne"},{"context":"$apiContextOne","version":"$apiVersionNbrOnePointOne"}]}"""
  }
}
