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

import org.scalatest.prop.TableDrivenPropertyChecks

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{LaxEmailAddress, UserId}
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class CollaboratorSpec extends BaseJsonFormattersSpec with TableDrivenPropertyChecks {
  import CollaboratorSpec.*

  "Collaborator" when {
    "as an administrator" should {
      import Admin.*

      "create an admin via a role" in {
        val result = Collaborator.apply(email, Collaborator.Role.Administrator, userId)
        result.isAdministrator shouldBe true
        result.isDeveloper shouldBe false
        result shouldBe Collaborators.Administrator(userId, email)
      }

      "describe it's role" in {
        example.describeRole shouldBe "ADMINISTRATOR"
      }

      "provide the role" in {
        example.role shouldBe Collaborator.Role.Administrator
      }

      "be an admin" in {
        example.isAdministrator shouldBe true
      }

      "but not a developer" in {
        example.isDeveloper shouldBe false
      }

      "produce json" in {
        testToJson[Collaborator](example)(
          ("role"         -> "ADMINISTRATOR"),
          ("userId"       -> userId.toString()),
          ("emailAddress" -> email.toString())
        )
      }

      "read json" in {
        testFromJson[Collaborator](s"""{"role":"ADMINISTRATOR","userId":"$userId","emailAddress":"$email"}""")(example)
      }
    }

    "as a general collaborator" should {
      import Dev.*

      "create a developer via a role" in {
        val result = Collaborator.apply(email, Collaborator.Role.Developer, userId)
        result.isAdministrator shouldBe false
        result.isDeveloper shouldBe true
        result shouldBe Collaborators.Developer(userId, email)
      }

      "describe it's role" in {
        example.describeRole shouldBe "DEVELOPER"
      }

      "provide the role" in {
        example.role shouldBe Collaborator.Role.Developer
      }

      "a developer" in {
        example.isDeveloper shouldBe !example.isAdministrator
        example.isDeveloper shouldBe true
      }

      "but not an admin" in {
        example.isAdministrator shouldBe false
      }

      "produce json" in {
        testToJson[Collaborator](example)(
          ("role"         -> "DEVELOPER"),
          ("userId"       -> userId.toString()),
          ("emailAddress" -> email.toString())
        )
      }

      "read json" in {
        testFromJson[Collaborator](s"""{"role":"DEVELOPER","userId":"$userId","emailAddress":"$email"}""")(example)
      }
    }

    "Roles" should {
      "convert from text" in {
        Collaborator.Role("DEVELOPER") shouldBe Some(Collaborator.Role.Developer)
        Collaborator.Role("ADMINISTRATOR") shouldBe Some(Collaborator.Role.Administrator)
        Collaborator.Role("bobbins") shouldBe None
      }

      "return the appropriate flags" in {
        Collaborator.Role.Administrator.isAdministrator shouldBe true
        Collaborator.Role.Developer.isAdministrator shouldBe false
        Collaborator.Role.Administrator.isDeveloper shouldBe false
        Collaborator.Role.Developer.isDeveloper shouldBe true
      }

      "displayText correctly" in {
        val values =
          Table(
            ("role", "displayText"),
            (Collaborator.Role.Administrator, "Administrator"),
            (Collaborator.Role.Developer, "Developer")
          )
        forAll(values) { (role, displayText) =>
          role.toString() shouldBe displayText
        }
      }

      "write admin to json" in {
        val admin: Collaborator.Role = Collaborator.Role.Administrator
        Json.toJson(admin) shouldBe JsString("ADMINISTRATOR")
      }
      "write developer to json" in {
        val admin: Collaborator.Role = Collaborator.Role.Developer
        Json.toJson(admin) shouldBe JsString("DEVELOPER")
      }
      "read admin from json" in {
        Json.fromJson[Collaborator.Role](JsString("ADMINISTRATOR")).get shouldBe Collaborator.Role.Administrator
      }
      "read developer from json" in {
        Json.fromJson[Collaborator.Role](JsString("DEVELOPER")).get shouldBe Collaborator.Role.Developer
      }
      "handle bad json" in {
        Json.fromJson[Collaborator.Role](JsString("bobbins")).asOpt shouldBe None
      }
    }
  }
}

object CollaboratorSpec {

  object Admin {
    val email    = LaxEmailAddress("bob@smith.com")
    val userId   = UserId.random
    val example  = Collaborators.Administrator(userId, email)
    val jsonText = s"""{"userId":"$userId","emailAddress":"$email","role":"ADMINISTRATOR"}"""
  }

  object Dev {
    val email    = LaxEmailAddress("fred@flintstone.com")
    val userId   = UserId.random
    val example  = Collaborators.Developer(userId, email)
    val jsonText = s"""{"userId":"$userId","emailAddress":"$email","role":"DEVELOPER"}"""
  }
}
