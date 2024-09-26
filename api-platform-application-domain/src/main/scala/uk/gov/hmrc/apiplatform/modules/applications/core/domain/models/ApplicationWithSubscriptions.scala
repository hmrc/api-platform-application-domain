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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

case class ApplicationWithSubscriptions(
    coreApp: CoreApplication,
    collaborators: Set[Collaborator],
    subscriptions: Set[ApiIdentifier]
  ) extends HasEnvironment with HasState with HasAccess with HasCollaborators {
  val id = coreApp.id

  private[core] lazy val deployedTo = coreApp.deployedTo
  private[core] lazy val state      = coreApp.state
  private[core] lazy val access     = coreApp.access
}

object ApplicationWithSubscriptions {
  import play.api.libs.json._

  private val transformOldResponse: OldExtendedApplicationResponse => ApplicationWithSubscriptions = (old) => {
    ApplicationWithSubscriptions(
      coreApp = CoreApplication(
        id = old.id,
        clientId = old.clientId,
        gatewayId = old.gatewayId,
        name = ApplicationName(old.name),
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
        allowAutoDelete = old.allowAutoDelete
      ),
      collaborators = old.collaborators,
      subscriptions = old.subscriptions.toSet
    )
  }

  val reads: Reads[ApplicationWithSubscriptions]            = Json.reads[ApplicationWithSubscriptions].orElse(Json.reads[OldExtendedApplicationResponse].map(transformOldResponse))
  val writes: Writes[ApplicationWithSubscriptions]          = Json.writes[ApplicationWithSubscriptions]
  implicit val format: Format[ApplicationWithSubscriptions] = Format(reads, writes)

  import monocle.Focus
  val coreApplicationF = Focus[ApplicationWithSubscriptions](_.coreApp)
  val collaboratorsF   = Focus[ApplicationWithSubscriptions](_.collaborators)
  val subscriptionsF   = Focus[ApplicationWithSubscriptions](_.subscriptions)
  val accessF          = coreApplicationF andThen CoreApplication.accessF
  val stateF           = coreApplicationF andThen CoreApplication.stateF
}
