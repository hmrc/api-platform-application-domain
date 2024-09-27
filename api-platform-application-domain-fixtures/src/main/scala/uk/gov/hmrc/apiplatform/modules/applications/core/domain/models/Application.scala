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

object CoreApplicationData
    extends ApplicationIdFixture
    with ApplicationStateFixture
    with ApplicationNameFixture
    with ClientIdFixture
    with IpAllowListFixture
    with AccessFixture
    with FixedClock {

  object Standard {

    val one = CoreApplication(
      id = applicationIdOne,
      clientId = clientIdOne,
      gatewayId = "abc123",
      name = appNameOne,
      deployedTo = Environment.PRODUCTION,
      description = None,
      createdOn = instant,
      lastAccess = Some(instant),
      grantLength = GrantLength.EIGHTEEN_MONTHS,
      lastAccessTokenUsage = Some(instant),
      access = stdAccess,
      state = appStateProduction,
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      ipAllowlist = defaultIpAllowList,
      allowAutoDelete = false
    )
  }

  object Privileged {

    val one = CoreApplication(
      id = applicationIdTwo,
      clientId = clientIdTwo,
      gatewayId = "def567",
      name = appNameTwo,
      deployedTo = Environment.PRODUCTION,
      description = None,
      createdOn = instant,
      lastAccess = Some(instant),
      grantLength = GrantLength.EIGHTEEN_MONTHS,
      lastAccessTokenUsage = Some(instant),
      access = privilegedAccess,
      state = appStateProduction,
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      ipAllowlist = defaultIpAllowList,
      allowAutoDelete = false
    )
  }

  object Ropc {

    val one = CoreApplication(
      id = applicationIdThree,
      clientId = clientIdThree,
      gatewayId = "def890",
      name = appNameThree,
      deployedTo = Environment.PRODUCTION,
      description = None,
      createdOn = instant,
      lastAccess = Some(instant),
      grantLength = GrantLength.EIGHTEEN_MONTHS,
      lastAccessTokenUsage = Some(instant),
      access = ropcAccess,
      state = appStateProduction,
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      ipAllowlist = defaultIpAllowList,
      allowAutoDelete = false
    )
  }
}

trait CoreApplicationFixture {
  val coreAppStdOne  = CoreApplicationData.Standard.one
  val coreAppPrivOne = CoreApplicationData.Privileged.one
  val coreAppRopcOne = CoreApplicationData.Ropc.one
}
