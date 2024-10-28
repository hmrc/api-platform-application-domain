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

  object Values {
    val requestedByName           = "john smith"
    val requestedByEmail          = "john.smith@example.com"
    val aVerificationCode: String = "verificationCode"
  }

  import Values._

  val testing = ApplicationState(
    name = TESTING,
    updatedOn = instant
  )

  val pendingGatekeeperApproval = ApplicationState(
    name = PENDING_GATEKEEPER_APPROVAL,
    updatedOn = instant,
    requestedByName = Some(requestedByName),
    requestedByEmailAddress = Some(requestedByEmail)
  )

  val pendingRequesterVerification = ApplicationState(
    name = PENDING_REQUESTER_VERIFICATION,
    updatedOn = instant,
    requestedByEmailAddress = Some(requestedByEmail),
    verificationCode = Some(aVerificationCode)
  )

  val pendingRIVerification = ApplicationState(
    name = PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION,
    updatedOn = instant,
    requestedByName = Some(requestedByName),
    requestedByEmailAddress = Some(requestedByEmail),
    verificationCode = Some(aVerificationCode)
  )

  val preProduction = ApplicationState(
    name = PRE_PRODUCTION,
    updatedOn = instant,
    requestedByEmailAddress = Some(requestedByEmail),
    verificationCode = Some(aVerificationCode)
  )

  val production = ApplicationState(
    name = PRODUCTION,
    updatedOn = instant,
    requestedByName = Some(requestedByName),
    requestedByEmailAddress = Some(requestedByEmail)
  )

  val deleted = ApplicationState(
    name = DELETED,
    updatedOn = instant,
    requestedByName = Some(requestedByName),
    requestedByEmailAddress = Some(requestedByEmail)
  )
}

trait ApplicationStateFixtures {
  val appStateTesting                      = ApplicationStateData.testing
  val appStatePendingGatekeeperApproval    = ApplicationStateData.pendingGatekeeperApproval
  val appStatePendingRequesterVerification = ApplicationStateData.pendingRequesterVerification
  val appStatePendingRIVerification        = ApplicationStateData.pendingRIVerification
  val appStatePreProduction                = ApplicationStateData.preProduction
  val appStateProduction                   = ApplicationStateData.production
  val appStateDeleted                      = ApplicationStateData.deleted

  val appStateRequestByName = ApplicationStateData.Values.requestedByName
  val appStateRequestByEmail = ApplicationStateData.Values.requestedByEmail
  val appStateVerificationCode = ApplicationStateData.Values.aVerificationCode
}
