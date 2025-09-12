/*
 * Copyright 2025 HM Revenue & Customs
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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ClientId, ClientIdData}
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

object ApplicationTokenData extends FixedClock {

  val one   = ApplicationToken(ClientIdData.one, "FAKETOKENONE", List(ClientSecretData.one), ClientSecretData.one.lastAccess)
  val two   = ApplicationToken(ClientIdData.two, "FAKETOKENTWO", List(ClientSecretData.two), ClientSecretData.two.lastAccess)
  val three = ApplicationToken(ClientIdData.three, "FAKETOKENTHREE", List(ClientSecretData.three), ClientSecretData.three.lastAccess)

  val privId = ClientId.random
  val ropcId = ClientId.random
  val priv   = ApplicationToken(privId, "FAKETOKENPRIV", List(ClientSecretData.priv), ClientSecretData.priv.lastAccess)
  val ropc   = ApplicationToken(ropcId, "FAKETOKENROPC", List(ClientSecretData.ropc), ClientSecretData.ropc.lastAccess)
}

trait ApplicationTokenFixtures extends ClientSecretFixtures {
  val applicationTokenOne   = ApplicationTokenData.one
  val applicationTokenTwo   = ApplicationTokenData.two
  val applicationTokenThree = ApplicationTokenData.three
  val applicationTokenPriv  = ApplicationTokenData.priv
  val applicationTokenRopc  = ApplicationTokenData.ropc
}
