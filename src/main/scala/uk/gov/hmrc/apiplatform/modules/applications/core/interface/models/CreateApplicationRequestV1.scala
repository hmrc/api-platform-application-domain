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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

case class CreateApplicationRequestV1 private (
    name: String,
    access: Access,
    description: Option[String],
    environment: Environment,
    collaborators: Set[Collaborator],
    subscriptions: Option[Set[ApiIdentifier]]
  ) extends CreateApplicationRequest {

  validate(this)

  lazy val accessType = access.accessType

  lazy val anySubscriptions: Set[ApiIdentifier] = subscriptions.getOrElse(Set.empty)

  private def validate(in: CreateApplicationRequestV1): Unit = {
    super.validate(in)
    in.access match {
      case a: Access.Standard => require(a.redirectUris.size <= 5, "maximum number of redirect URIs exceeded")
      case _                  =>
    }
  }

}

object CreateApplicationRequestV1 {

  def create(
      name: String,
      access: Access,
      description: Option[String],
      environment: Environment,
      collaborators: Set[Collaborator],
      subscriptions: Option[Set[ApiIdentifier]]
    ): CreateApplicationRequestV1 = new CreateApplicationRequestV1(name, access, description, environment, collaborators, subscriptions)

  import play.api.libs.json._
  implicit val format: OFormat[CreateApplicationRequestV1] = Json.format[CreateApplicationRequestV1]
}
