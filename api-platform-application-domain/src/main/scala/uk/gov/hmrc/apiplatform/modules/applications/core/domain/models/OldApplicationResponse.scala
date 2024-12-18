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

import java.time.Instant

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._

private[models] case class OldApplicationResponse(
    id: ApplicationId,
    clientId: ClientId,
    gatewayId: String,
    name: ApplicationName,
    deployedTo: Environment,
    description: Option[String],
    collaborators: Set[Collaborator],
    createdOn: Instant,
    lastAccess: Option[Instant],
    grantLength: GrantLength,
    lastAccessTokenUsage: Option[Instant],
    access: Access,
    state: ApplicationState,
    rateLimitTier: RateLimitTier,
    checkInformation: Option[CheckInformation],
    blocked: Boolean,
    ipAllowlist: IpAllowlist,
    moreApplication: Option[MoreApplication] // APM drops this
  )
