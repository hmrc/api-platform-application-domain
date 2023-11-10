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

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

/*
** This is only used for creating an app when uplifting a standard sandbox app to production
 */
case class CreateApplicationRequestV2 private (
    name: String,
    access: StandardAccessDataToCopy,
    description: Option[String],
    environment: Environment,
    collaborators: Set[Collaborator],
    upliftRequest: UpliftRequest,
    requestedBy: String,
    sandboxApplicationId: ApplicationId
  ) extends CreateApplicationRequest {

  lazy val accessType = AccessType.STANDARD

  lazy val anySubscriptions: Set[ApiIdentifier] = upliftRequest.subscriptions

  def normaliseCollaborators: CreateApplicationRequestV2 = copy(collaborators = CreateApplicationRequest.normaliseEmails(collaborators))

}

object CreateApplicationRequestV2 {
  import play.api.libs.json.Json

  def create(
      name: String,
      access: StandardAccessDataToCopy,
      description: Option[String],
      environment: Environment,
      collaborators: Set[Collaborator],
      upliftRequest: UpliftRequest,
      requestedBy: String,
      sandboxApplicationId: ApplicationId
    ): Option[CreateApplicationRequestV2] = {

    CreateApplicationRequest.validateBasics(name, access.redirectUris, collaborators).fold(
      _ => None,
      { case (n, r, c) => Some(new CreateApplicationRequestV2(n, access, description, environment, c, upliftRequest, requestedBy, sandboxApplicationId)) }
    )
  }

  implicit val reads = Json.reads[CreateApplicationRequestV2]
}
