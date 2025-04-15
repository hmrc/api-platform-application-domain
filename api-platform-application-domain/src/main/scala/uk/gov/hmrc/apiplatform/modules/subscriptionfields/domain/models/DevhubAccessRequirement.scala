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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.Collaborator._

sealed trait DevhubAccessRequirement

object DevhubAccessRequirement {
  final val Default: DevhubAccessRequirement = Anyone

  case object NoOne     extends DevhubAccessRequirement
  case object AdminOnly extends DevhubAccessRequirement
  case object Anyone    extends DevhubAccessRequirement

  import play.api.libs.json._

  implicit val formatDevhubAccessRequirement: Format[DevhubAccessRequirement] = new Format[DevhubAccessRequirement] {

    override def writes(o: DevhubAccessRequirement): JsValue = JsString(o match {
      case AdminOnly => "adminOnly"
      case Anyone    => "anyone"
      case NoOne     => "noOne"
    })

    override def reads(json: JsValue): JsResult[DevhubAccessRequirement] = json match {
      case JsString("adminOnly") => JsSuccess(AdminOnly)
      case JsString("anyone")    => JsSuccess(Anyone)
      case JsString("noOne")     => JsSuccess(NoOne)
      case _                     => JsError("Not a recognized DevhubAccessRequirement")
    }
  }
}

sealed trait DevhubAccessLevel {
  def satisfiesRequirement(requirement: DevhubAccessRequirement): Boolean = DevhubAccessLevel.satisfies(requirement)(this)
}

object DevhubAccessLevel {

  def fromRole(role: Role): DevhubAccessLevel = role match {
    case Roles.ADMINISTRATOR => DevhubAccessLevel.Admininstator
    case Roles.DEVELOPER     => DevhubAccessLevel.Developer
  }

  case object Developer     extends DevhubAccessLevel
  case object Admininstator extends DevhubAccessLevel

  import DevhubAccessRequirement._

  def satisfies(requirement: DevhubAccessRequirement)(actual: DevhubAccessLevel): Boolean = (requirement, actual) match {
    case (NoOne, _)             => false
    case (AdminOnly, Developer) => false
    case _                      => true
  }

}
