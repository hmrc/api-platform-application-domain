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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.UserId

trait HasCollaborators {
  self: { def collaborators: Set[Collaborator] } =>

  lazy val admins: Set[Collaborator] = collaborators.filter(_.isAdministrator)

  def roleFor(userId: UserId): Option[Collaborator.Role] = collaborators.find(_.userId == userId).map(_.role)
  def isCollaborator(userId: UserId): Boolean            = roleFor(userId).isDefined
}

case class ApplicationWithCollaborators(
    coreApp: CoreApplication,
    collaborators: Set[Collaborator]
  ) extends HasEnvironment with HasState with HasAccess with HasCollaborators {
  val id = coreApp.id

  private[core] lazy val deployedTo = coreApp.deployedTo
  lazy val state                    = coreApp.state
  lazy val access                   = coreApp.access
}

object ApplicationWithCollaborators {
  import play.api.libs.json._

  private val transformOldResponse: OldApplicationResponse => ApplicationWithCollaborators = (old) => {
    ApplicationWithCollaborators(
      coreApp = CoreApplication(
        id = old.id,
        clientId = old.clientId,
        gatewayId = old.gatewayId,
        name = old.name,
        deployedTo = old.deployedTo,
        description = old.description,
        createdOn = old.createdOn,
        lastAccess = old.lastAccess,
        grantLength = old.grantLength,
        lastAccessTokenUsage = old.lastAccessTokenUsage,
        access = old.access,
        state = old.state,
        rateLimitTier = old.rateLimitTier,
        checkInformation = old.checkInformation,
        blocked = old.blocked,
        ipAllowlist = old.ipAllowlist,
        allowAutoDelete = old.moreApplication.allowAutoDelete
      ),
      collaborators = old.collaborators
    )
  }

  val reads: Reads[ApplicationWithCollaborators]            = Json.reads[ApplicationWithCollaborators].orElse(Json.reads[OldApplicationResponse].map(transformOldResponse))
  val writes: Writes[ApplicationWithCollaborators]          = Json.writes[ApplicationWithCollaborators]
  implicit val format: Format[ApplicationWithCollaborators] = Format(reads, writes)
}
