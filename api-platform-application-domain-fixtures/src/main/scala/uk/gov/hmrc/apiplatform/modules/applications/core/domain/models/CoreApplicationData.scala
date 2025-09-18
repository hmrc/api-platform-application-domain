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
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

object CoreApplicationData extends FixedClock {
  val appDescription = "A description"

  object Standard {

    val one = CoreApplication(
      id = ApplicationIdData.one,
      token = ApplicationTokenData.one,
      gatewayId = "abc123",
      name = ApplicationNameData.one,
      deployedTo = Environment.PRODUCTION,
      description = Some(appDescription),
      createdOn = instant,
      lastAccess = Some(instant),
      grantLength = GrantLength.EIGHTEEN_MONTHS,
      access = AccessData.Standard.default,
      state = ApplicationStateData.production,
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      ipAllowlist = IpAllowListData.one,
      lastActionActor = ActorType.UNKNOWN,
      deleteRestriction = DeleteRestriction.NoRestriction,
      organisationId = None
    )

    val two = one.copy(
      id = ApplicationIdData.two,
      token = ApplicationTokenData.two,
      name = ApplicationNameData.two
    )

    val three = one.copy(
      id = ApplicationIdData.three,
      token = ApplicationTokenData.three,
      name = ApplicationNameData.three
    )
  }

  object Privileged {

    val one = CoreApplication(
      id = ApplicationIdData.two,
      token = ApplicationTokenData.priv,
      gatewayId = "def567",
      name = ApplicationNameData.two,
      deployedTo = Environment.PRODUCTION,
      description = None,
      createdOn = instant,
      lastAccess = Some(instant),
      grantLength = GrantLength.EIGHTEEN_MONTHS,
      access = AccessData.Privileged.default,
      state = ApplicationStateData.production,
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      ipAllowlist = IpAllowListData.one,
      lastActionActor = ActorType.UNKNOWN,
      deleteRestriction = DeleteRestriction.NoRestriction,
      organisationId = None
    )
  }

  object Ropc {

    val one = CoreApplication(
      id = ApplicationIdData.three,
      token = ApplicationTokenData.ropc,
      gatewayId = "def890",
      name = ApplicationNameData.three,
      deployedTo = Environment.PRODUCTION,
      description = None,
      createdOn = instant,
      lastAccess = Some(instant),
      grantLength = GrantLength.EIGHTEEN_MONTHS,
      access = AccessData.Ropc.default,
      state = ApplicationStateData.production,
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      ipAllowlist = IpAllowListData.one,
      lastActionActor = ActorType.UNKNOWN,
      deleteRestriction = DeleteRestriction.NoRestriction,
      organisationId = None
    )
  }
}

trait CoreApplicationFixtures
    extends ApplicationIdFixtures
    with ClientIdFixtures
    with ApplicationNameFixtures
    with AccessFixtures
    with ApplicationStateFixtures
    with CheckInformationFixtures
    with ApplicationTokenFixtures
    with IpAllowListFixtures {

  val standardCoreApp   = CoreApplicationData.Standard.one
  val standardCoreApp2  = CoreApplicationData.Standard.two
  val standardCoreApp3  = CoreApplicationData.Standard.three
  val privilegedCoreApp = CoreApplicationData.Privileged.one
  val ropcCoreApp       = CoreApplicationData.Ropc.one

  implicit class CoreApplicationFixtureSyntax(app: CoreApplication) {
    import monocle.syntax.all._
    def withId(anId: ApplicationId): CoreApplication       = app.focus(_.id).replace(anId)
    def withName(aName: ApplicationName): CoreApplication  = app.focus(_.name).replace(aName)
    def withEnvironment(env: Environment): CoreApplication = app.focus(_.deployedTo).replace(env)
  }
}
