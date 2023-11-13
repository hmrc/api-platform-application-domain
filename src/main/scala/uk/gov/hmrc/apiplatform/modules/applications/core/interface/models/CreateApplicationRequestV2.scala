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

  validate(this)

  lazy val accessType = AccessType.STANDARD

  lazy val anySubscriptions: Set[ApiIdentifier] = upliftRequest.subscriptions

  def normaliseCollaborators: CreateApplicationRequestV2 = copy(collaborators = CreateApplicationRequest.normaliseEmails(collaborators))

}

object CreateApplicationRequestV2 {

  def create(
      name: String,
      access: StandardAccessDataToCopy,
      description: Option[String],
      environment: Environment,
      collaborators: Set[Collaborator],
      upliftRequest: UpliftRequest,
      requestedBy: String,
      sandboxApplicationId: ApplicationId
    ): CreateApplicationRequestV2 = {

    val request = new CreateApplicationRequestV2(name, access, description, environment, collaborators, upliftRequest, requestedBy, sandboxApplicationId)

    request.copy(collaborators = CreateApplicationRequest.normaliseEmails(request.collaborators))

    // CreateApplicationRequest.validateBasics(name, access.redirectUris, collaborators).fold(
    //   _ => None,
    //   { case (normalisedCollaborators) => Some(new CreateApplicationRequestV2(name, access, description, environment, normalisedCollaborators, upliftRequest, requestedBy, sandboxApplicationId)) }
    // )
  }

  // def isValid(request: CreateApplicationRequestV2): Boolean = {
  //   CreateApplicationRequest.validateBasics(request.name, request.access.redirectUris, request.collaborators).isRight
  // }

  import play.api.libs.json._
  implicit val format: OFormat[CreateApplicationRequestV2] = Json.format[CreateApplicationRequestV2]

  // implicit val reads: Reads[CreateApplicationRequestV2] = Json.reads[CreateApplicationRequestV2].filter(r => CreateApplicationRequestV2.isValid(r))
  // implicit val writes: OWrites[CreateApplicationRequestV2] = Json.writes[CreateApplicationRequestV2]
}
