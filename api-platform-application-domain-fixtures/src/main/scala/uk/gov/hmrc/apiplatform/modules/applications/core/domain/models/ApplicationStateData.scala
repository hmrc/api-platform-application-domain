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

import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.State._

object ApplicationStateData extends FixedClock {
  val testing                      = ApplicationState(name = TESTING, updatedOn = instant)
  val pendingGatekeeperApproval    = ApplicationState(name = PENDING_GATEKEEPER_APPROVAL, updatedOn = instant)
  val pendingRequesterVerification = ApplicationState(name = PENDING_REQUESTER_VERIFICATION, updatedOn = instant)
  val pendingRIVerification        = ApplicationState(name = PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION, updatedOn = instant)
  val preProduction                = ApplicationState(name = PRE_PRODUCTION, updatedOn = instant)
  val production                   = ApplicationState(name = PRODUCTION, updatedOn = instant)
  val deleted                      = ApplicationState(name = DELETED, updatedOn = instant)
}

trait ApplicationStateFixtures {
  val appStateTesting                      = ApplicationStateData.testing
  val appStatePendingGatekeeperApproval    = ApplicationStateData.pendingGatekeeperApproval
  val appStatePendingRequesterVerification = ApplicationStateData.pendingRequesterVerification
  val appStatePendingRIVerification        = ApplicationStateData.pendingRIVerification
  val appStatePreProduction                = ApplicationStateData.preProduction
  val appStateProduction                   = ApplicationStateData.production
  val appStateDeleted                      = ApplicationStateData.deleted
}
