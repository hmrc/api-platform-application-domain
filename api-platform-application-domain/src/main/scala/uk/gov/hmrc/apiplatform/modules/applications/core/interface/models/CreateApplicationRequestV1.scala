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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

case class CreateApplicationRequestV1(
    name: ApplicationName,
    access: CreationAccess,
    description: Option[String],
    environment: Environment,
    collaborators: Set[Collaborator],
    subscriptions: Option[Set[ApiIdentifier]]
  ) extends CreateApplicationRequest {

  super.validate(this)

  lazy val accessType = access.accessType

  lazy val anySubscriptions: Set[ApiIdentifier] = subscriptions.getOrElse(Set.empty)
}

object CreateApplicationRequestV1 {

  import play.api.libs.json._

  private def handleValidation(car: CreateApplicationRequestV1): JsResult[CreateApplicationRequestV1] = {
    Try(car.validate(car)).fold(err => JsError(err.getMessage), _ => JsSuccess(car))
  }

  private val reads: Reads[CreateApplicationRequestV1]     = Json.reads[CreateApplicationRequestV1].flatMapResult(handleValidation(_))
  private val writes: Writes[CreateApplicationRequestV1]   = Json.writes[CreateApplicationRequestV1]
  implicit val format1: Format[CreateApplicationRequestV1] = Format(reads, writes)
}
