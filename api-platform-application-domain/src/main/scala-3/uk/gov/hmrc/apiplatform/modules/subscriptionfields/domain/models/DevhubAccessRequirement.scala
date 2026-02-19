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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.Collaborator

enum DevhubAccessRequirement {
  case NoOne, AdminOnly, Anyone
}

object DevhubAccessRequirement {
  final val Default: DevhubAccessRequirement = Anyone

  import play.api.libs.json.*

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

enum DevhubAccessLevel {
  case Developer, Admininstator
}

object DevhubAccessLevel {

  extension (dal: DevhubAccessLevel) {
    def satisfiesRequirement(requirement: DevhubAccessRequirement): Boolean = DevhubAccessLevel.satisfies(requirement)(dal)
  }

  def fromRole(role: Collaborator.Role): DevhubAccessLevel = role match {
    case Collaborator.Role.Administrator => DevhubAccessLevel.Admininstator
    case Collaborator.Role.Developer     => DevhubAccessLevel.Developer
  }

  import DevhubAccessRequirement.*

  def satisfies(requirement: DevhubAccessRequirement)(actual: DevhubAccessLevel): Boolean = (requirement, actual) match {
    case (NoOne, _)             => false
    case (AdminOnly, Developer) => false
    case _                      => true
  }

}
