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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ActorFixtures, ApplicationIdFixtures}
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

object StateHistoryData extends FixedClock with ApplicationIdFixtures with ActorFixtures {
  val aStateHistoryTesting             = StateHistory(applicationIdOne, State.TESTING, collaboratorActorOne, None, None, instant)
  val aStateHistoryPreProd             = StateHistory(applicationIdOne, State.PRE_PRODUCTION, collaboratorActorOne, None, None, instant)
  val aStateHistoryProd                = StateHistory(applicationIdOne, State.PRODUCTION, collaboratorActorOne, None, None, instant)
  val aStateHistoryDeleted             = StateHistory(applicationIdOne, State.DELETED, collaboratorActorOne, None, None, instant)
  val aStateHistoryPendingGK           = StateHistory(applicationIdOne, State.PENDING_GATEKEEPER_APPROVAL, collaboratorActorOne, None, None, instant)
  val aStateHistoryPendingVerification = StateHistory(applicationIdOne, State.PENDING_REQUESTER_VERIFICATION, collaboratorActorOne, None, None, instant)
  val aStateHistoryPendingRI           = StateHistory(applicationIdOne, State.PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION, collaboratorActorOne, None, None, instant)
}

trait StateHistoryFixtures {
  val aStateHistoryTesting             = StateHistoryData.aStateHistoryTesting
  val aStateHistoryPreProd             = StateHistoryData.aStateHistoryPreProd
  val aStateHistoryProd                = StateHistoryData.aStateHistoryProd
  val aStateHistoryPendingGK           = StateHistoryData.aStateHistoryPendingGK
  val aStateHistoryPendingVerification = StateHistoryData.aStateHistoryPendingVerification
  val aStateHistoryPendingRI           = StateHistoryData.aStateHistoryPendingRI
}
