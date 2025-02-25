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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import scala.util.Try

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

/*
** This is only used for creating an app when uplifting a standard sandbox app to production
 */
case class CreateApplicationRequestV2 private (
    name: ApplicationName,
    access: StandardAccessDataToCopy,
    description: Option[String],
    environment: Environment,
    collaborators: Set[Collaborator],
    upliftRequest: UpliftRequest,
    requestedBy: String,
    sandboxApplicationId: ApplicationId
  ) extends CreateApplicationRequest {

  validate()

  lazy val accessType = AccessType.STANDARD

  lazy val anySubscriptions: Set[ApiIdentifier] = upliftRequest.subscriptions

  private def validate(): Unit = {
    super.validate(this)
    require(access.redirectUris.size <= 5, "maximum number of login redirect URIs exceeded")
    require(access.postLogoutRedirectUris.size <= 5, "maximum number of post logout redirect URIs exceeded")
  }
}

object CreateApplicationRequestV2 {

  def create(
      name: ApplicationName,
      access: StandardAccessDataToCopy,
      description: Option[String],
      environment: Environment,
      collaborators: Set[Collaborator],
      upliftRequest: UpliftRequest,
      requestedBy: String,
      sandboxApplicationId: ApplicationId
    ): CreateApplicationRequestV2 = CreateApplicationRequestV2(name, access, description, environment, collaborators, upliftRequest, requestedBy, sandboxApplicationId)

  import play.api.libs.json._

  private def handleValidation(car: CreateApplicationRequestV2): JsResult[CreateApplicationRequestV2] = {
    Try(car.validate(car)).fold(err => JsError(err.getMessage), _ => JsSuccess(car))
  }

  private val reads: Reads[CreateApplicationRequestV2]     = Json.reads[CreateApplicationRequestV2].flatMapResult(handleValidation(_))
  private val writes: Writes[CreateApplicationRequestV2]   = Json.writes[CreateApplicationRequestV2]
  implicit val format2: Format[CreateApplicationRequestV2] = Format(reads, writes)
}
