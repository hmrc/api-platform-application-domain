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
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, UserId}
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationWithSubscriptionsData.someSubscriptions
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

    "uplifts to with subscriptions" in {
      example.withSubscriptions(someSubscriptions) shouldBe ApplicationWithSubscriptions(example.details, example.collaborators, someSubscriptions)
    }

    "return the admins" in {
      val app = example.copy(collaborators = Set(Admin.example, Dev.example))
      app.admins shouldBe Set(Admin.example)
    }

    "identify a collaborator" in {
      val app = example.copy(collaborators = Set(Dev.example, Admin.example))
      app.isCollaborator(Dev.example.userId) shouldBe true
      app.isAdministrator(Dev.example.userId) shouldBe false
    }

    "identify an admin" in {
      val app = example.copy(collaborators = Set(Dev.example, Admin.example))
      app.isCollaborator(Admin.example.userId) shouldBe true
      app.isAdministrator(Admin.example.userId) shouldBe true
    }

    "identify a random user correctly" in {
      val app = example.copy(collaborators = Set(Dev.example, Admin.example))
      app.isCollaborator(UserId.random) shouldBe false
      app.isAdministrator(UserId.random) shouldBe false
    }

    "identify a role" in {
      val app = example.copy(collaborators = Set(Dev.example, Admin.example))
      app.roleFor(Dev.example.userId).value shouldBe Collaborator.Roles.DEVELOPER
      app.roleFor(Admin.example.userId).value shouldBe Collaborator.Roles.ADMINISTRATOR
    }

    "identify a role by email" in {
      val app = example.copy(collaborators = Set(Dev.example, Admin.example))
      app.roleFor(Dev.example.emailAddress).value shouldBe Collaborator.Roles.DEVELOPER
      app.roleFor(Admin.example.emailAddress).value shouldBe Collaborator.Roles.ADMINISTRATOR
      app.roleFor(UserId.random) shouldBe None
    }

    "orders correctly" in {
      val apps = List("a", "b", "c", "d", "e", "f", "g").map(ApplicationName(_)).map(n => standardApp.withId(ApplicationId.random).modify(_.copy(name = n)))

      val rnd = Random.shuffle(apps)
      rnd should not be apps
      rnd.sorted shouldBe apps
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

      newApp.details.isInPreProductionOrProduction shouldBe true
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
