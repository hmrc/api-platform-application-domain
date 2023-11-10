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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access.Standard
import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

case class CreateApplicationRequestV1 private (
    name: String,
    access: Access,
    description: Option[String],
    environment: Environment,
    collaborators: Set[Collaborator],
    subscriptions: Option[Set[ApiIdentifier]]
  ) extends CreateApplicationRequest {

  lazy val accessType = access.accessType

  lazy val anySubscriptions: Set[ApiIdentifier] = subscriptions.getOrElse(Set.empty)

  def normaliseCollaborators: CreateApplicationRequestV1 = copy(collaborators = CreateApplicationRequest.normaliseEmails(collaborators))

}

object CreateApplicationRequestV1 {
  import play.api.libs.json.Json

  def create(
      name: String,
      access: Access,
      description: Option[String],
      environment: Environment,
      collaborators: Set[Collaborator],
      upliftRequest: UpliftRequest,
      requestedBy: String,
      sandboxApplicationId: ApplicationId
    ): Option[CreateApplicationRequestV1] = {
    val redirectUris = access match {
      case Standard(redirectUris, _, _, _, _, _) => redirectUris
      case _                                     => List.empty
    }

    CreateApplicationRequest.validateBasics(name, redirectUris, collaborators).fold(
      _ => None,
      { case (n, r, c) => Some(new CreateApplicationRequestV1(n, access, description, environment, c, None)) }
    )
  }

  implicit val reads = Json.reads[CreateApplicationRequestV1]
}
