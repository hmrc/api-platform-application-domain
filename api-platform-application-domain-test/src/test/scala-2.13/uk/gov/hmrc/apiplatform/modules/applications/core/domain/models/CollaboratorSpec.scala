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
  import CollaboratorSpec._

  "Collaborator" when {
    "as an administrator" should {
      import Admin._

      "create an admin via a role" in {
        val result = Collaborator.apply(email, Collaborator.Roles.ADMINISTRATOR, userId)
        result.isAdministrator shouldBe true
        result.isDeveloper shouldBe false
        result shouldBe Collaborators.Administrator(userId, email)
      }

      "describe it's role" in {
        example.describeRole shouldBe "ADMINISTRATOR"
      }

      "provide the role" in {
        example.role shouldBe Collaborator.Roles.ADMINISTRATOR
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
      import Dev._

      "create a developer via a role" in {
        val result = Collaborator.apply(email, Collaborator.Roles.DEVELOPER, userId)
        result.isAdministrator shouldBe false
        result.isDeveloper shouldBe true
        result shouldBe Collaborators.Developer(userId, email)
      }

      "describe it's role" in {
        example.describeRole shouldBe "DEVELOPER"
      }

      "provide the role" in {
        example.role shouldBe Collaborator.Roles.DEVELOPER
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

      "displayText correctly" in {
        val values =
          Table(
            ("role", "displayText"),
            (Collaborator.Roles.ADMINISTRATOR, "Administrator"),
            (Collaborator.Roles.DEVELOPER, "Developer")
          )
        forAll(values) { (role, displayText) =>
          role.displayText shouldBe displayText
        }
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
