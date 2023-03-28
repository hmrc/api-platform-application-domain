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

package uk.gov.hmrc.apiplatform.modules.applications.domain.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import play.api.libs.json.{JsString, Json}

import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress
import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec
import uk.gov.hmrc.apiplatform.modules.developers.domain.models.UserId

class CollaboratorSpec extends AnyWordSpec with Matchers with JsonFormattersSpec {

  "Collborator" when {
    "creating" should {
      val email  = LaxEmailAddress("bob")
      val userId = UserId.random

      "create an administrator" in {
        val result = Collaborators.Administrator(userId, email)
        result.isAdministrator shouldBe true
        result.isDeveloper shouldBe false
        result shouldBe Collaborators.Administrator(userId, email)
      }
      "create a developer" in {
        val result = Collaborators.Developer(userId, email)
        result.isAdministrator shouldBe false
        result.isDeveloper shouldBe true
        result shouldBe Collaborators.Developer(userId, email)
      }
      "creating an admin via a role" in {
        val result = Collaborator.apply(email, Collaborator.Roles.ADMINISTRATOR, userId)
        result.isAdministrator shouldBe true
        result shouldBe Collaborators.Administrator(userId, email)
      }
      "creating a dev via a role" in {
        val result = Collaborator.apply(email, Collaborator.Roles.DEVELOPER, userId)
        result.isAdministrator shouldBe false
        result shouldBe Collaborators.Developer(userId, email)
      }
    }

    "given an administrator" should {
      val anId                = UserId.random
      val idAsText            = anId.value.toString()
      val anEmail             = LaxEmailAddress("bob@smith.com")
      val admin: Collaborator = Collaborators.Administrator(anId, anEmail)

      "describe it's role" in {
        admin.describeRole shouldBe "ADMINISTRATOR"
      }

      "provide the role" in {
        admin.role shouldBe Collaborator.Roles.ADMINISTRATOR
      }

      "normalise an email address" in {
        val mixedCaseEmail      = LaxEmailAddress(anEmail.text.capitalize)
        val admin: Collaborator = Collaborators.Administrator(anId, mixedCaseEmail)

        admin.normalise.emailAddress shouldBe anEmail
      }

      "be an admin" in {
        admin.isAdministrator shouldBe true
      }

      "but not a developer" in {
        admin.isDeveloper shouldBe false
      }

      "produce json" in {
        testToJson[Collaborator](admin)(
          ("role"         -> "ADMINISTRATOR"),
          ("userId"       -> idAsText),
          ("emailAddress" -> "bob@smith.com")
        )
      }

      "read json" in {
        testFromJson[Collaborator](s"""{"role":"ADMINISTRATOR","userId":"$idAsText","emailAddress":"bob@smith.com"}""")(admin)
      }

    }

    "given an developer" should {
      val anId                    = UserId.random
      val idAsText                = anId.value.toString()
      val anEmail                 = LaxEmailAddress("bob@smith.com")
      val developer: Collaborator = Collaborators.Developer(anId, anEmail)

      "describe it's role" in {
        developer.describeRole shouldBe "DEVELOPER"
      }

      "provide the role" in {
        developer.role shouldBe Collaborator.Roles.DEVELOPER
      }

      "normalise an email address" in {
        val mixedCaseEmail          = LaxEmailAddress(anEmail.text.capitalize)
        val developer: Collaborator = Collaborators.Developer(anId, mixedCaseEmail)

        developer.normalise.emailAddress shouldBe anEmail
      }

      "a developer" in {
        developer.isDeveloper shouldBe !developer.isAdministrator
        developer.isDeveloper shouldBe true
      }

      "but not an admin" in {
        developer.isAdministrator shouldBe false
      }

      "produce json" in {
        testToJson[Collaborator](developer)(
          ("role"         -> "DEVELOPER"),
          ("userId"       -> idAsText),
          ("emailAddress" -> "bob@smith.com")
        )
      }

      "read json" in {
        testFromJson[Collaborator](s"""{"role":"DEVELOPER","userId":"$idAsText","emailAddress":"bob@smith.com"}""")(developer)
      }
    }

    "Roles" should {
      "convert from text" in {
        Collaborator.Role("DEVELOPER") shouldBe Some(Collaborator.Roles.DEVELOPER)
        Collaborator.Role("ADMINISTRATOR") shouldBe Some(Collaborator.Roles.ADMINISTRATOR)
        Collaborator.Role("bobbins") shouldBe None
      }

      "return the appropriate flags" in {
        Collaborator.Roles.ADMINISTRATOR.isAdministrator shouldBe true
        Collaborator.Roles.DEVELOPER.isAdministrator shouldBe false
        Collaborator.Roles.ADMINISTRATOR.isDeveloper shouldBe false
        Collaborator.Roles.DEVELOPER.isDeveloper shouldBe true
      }

      "write admin to json" in {
        val admin: Collaborator.Role = Collaborator.Roles.ADMINISTRATOR
        Json.toJson(admin) shouldBe JsString("ADMINISTRATOR")
      }
      "write developer to json" in {
        val admin: Collaborator.Role = Collaborator.Roles.DEVELOPER
        Json.toJson(admin) shouldBe JsString("DEVELOPER")
      }
      "read admin from json" in {
        Json.fromJson[Collaborator.Role](JsString("ADMINISTRATOR")).get shouldBe Collaborator.Roles.ADMINISTRATOR
      }
      "read developer from json" in {
        Json.fromJson[Collaborator.Role](JsString("DEVELOPER")).get shouldBe Collaborator.Roles.DEVELOPER
      }
    }
  }
}
