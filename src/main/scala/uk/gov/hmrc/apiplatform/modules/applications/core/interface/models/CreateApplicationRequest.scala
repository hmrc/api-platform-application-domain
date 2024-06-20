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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

trait CreateApplicationRequest {
  def name: ValidatedApplicationName
  def description: Option[String]
  def collaborators: Set[Collaborator]
  def environment: Environment
  def anySubscriptions: Set[ApiIdentifier]

  def accessType: AccessType

  def validate(in: CreateApplicationRequest): Unit = {
    require(in.collaborators.exists(_.isAdministrator), "at least one ADMINISTRATOR collaborator is required")
    require(in.collaborators.toList.map(_.emailAddress).size == collaborators.map(_.emailAddress).size, "duplicate email in collaborator")
  }
}

object CreateApplicationRequest {
  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads

  private val readsV1: Reads[CreateApplicationRequestV1] = CreateApplicationRequestV1.format.reads _
  private val readsV2: Reads[CreateApplicationRequestV2] = CreateApplicationRequestV2.format.reads _

  implicit val reads: Reads[CreateApplicationRequest] =
    readsV2.map(_.asInstanceOf[CreateApplicationRequest]) or readsV1.map(_.asInstanceOf[CreateApplicationRequest])

}
