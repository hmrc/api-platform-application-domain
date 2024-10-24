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

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{LaxEmailAddress, UserId}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.Collaborator.Roles._

sealed trait Collaborator {
  def userId: UserId
  def emailAddress: LaxEmailAddress

  def isAdministrator: Boolean
  def isDeveloper: Boolean = !isAdministrator

  final def describeRole: String = Collaborator.describeRole(this)

  final def role: Collaborator.Role = Collaborator.role(this)
}

object Collaborator {

  sealed trait Role {
    def isAdministrator: Boolean
    def isDeveloper: Boolean = !isAdministrator

    def displayText: String = Role.displayText(this)
  }

  object Role {

    val displayText: Role => String = {
      case Collaborator.Roles.ADMINISTRATOR => "Administrator"
      case Collaborator.Roles.DEVELOPER     => "Developer"
    }

    def apply(text: String): Option[Collaborator.Role] = text.toUpperCase() match {
      case "ADMINISTRATOR" => Some(Collaborator.Roles.ADMINISTRATOR)
      case "DEVELOPER"     => Some(Collaborator.Roles.DEVELOPER)
      case _               => None
    }

    private val convert: String => JsResult[Role] = (s) => Role(s).fold[JsResult[Role]](JsError(s"$s is not a role"))(role => JsSuccess(role))

    implicit val reads: Reads[Role] = (JsPath.read[String]).flatMapResult(convert(_))

    implicit val writes: Writes[Role] = Writes[Role](role => JsString(role.toString))

    implicit val format: Format[Role] = Format(reads, writes)
  }

  object Roles {
    case object ADMINISTRATOR extends Role { val isAdministrator = true  }
    case object DEVELOPER     extends Role { val isAdministrator = false }
  }

  def apply(emailAddress: LaxEmailAddress, role: Role, userId: UserId): Collaborator = {
    role match {
      case ADMINISTRATOR => Collaborators.Administrator(userId, emailAddress)
      case DEVELOPER     => Collaborators.Developer(userId, emailAddress)
    }
  }

  def role(me: Collaborator): Collaborator.Role = me match {
    case a: Collaborators.Administrator => Collaborator.Roles.ADMINISTRATOR
    case d: Collaborators.Developer     => Collaborator.Roles.DEVELOPER
  }

  def describeRole(me: Collaborator): String = me match {
    case a: Collaborators.Administrator => Roles.ADMINISTRATOR.toString
    case d: Collaborators.Developer     => Roles.DEVELOPER.toString
  }

  import play.api.libs.json.Json
  import play.api.libs.json.OFormat
  import uk.gov.hmrc.play.json.Union

  implicit val formatAdministrator: OFormat[Collaborators.Administrator] = Json.format[Collaborators.Administrator]
  implicit val formatDeveloper: OFormat[Collaborators.Developer]         = Json.format[Collaborators.Developer]

  implicit val collaboratorJf: OFormat[Collaborator] = Union.from[Collaborator]("role")
    .and[Collaborators.Administrator](Roles.ADMINISTRATOR.toString)
    .and[Collaborators.Developer](Roles.DEVELOPER.toString)
    .format
}

object Collaborators {

  case class Administrator(userId: UserId, emailAddress: LaxEmailAddress) extends Collaborator {
    val isAdministrator = true
  }

  case class Developer(userId: UserId, emailAddress: LaxEmailAddress) extends Collaborator {
    val isAdministrator = false
  }
}
