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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.*
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

object StateHistoryData extends FixedClock {
  val aStateHistoryTesting             = StateHistory(ApplicationIdData.one, State.Testing, ActorData.Collaborators.one, None, None, instant)
  val aStateHistoryPreProd             = StateHistory(ApplicationIdData.one, State.PreProduction, ActorData.Collaborators.one, None, None, instant)
  val aStateHistoryProd                = StateHistory(ApplicationIdData.one, State.Production, ActorData.Collaborators.one, None, None, instant)
  val aStateHistoryDeleted             = StateHistory(ApplicationIdData.one, State.Deleted, ActorData.Collaborators.one, None, None, instant)
  val aStateHistoryPendingGK           = StateHistory(ApplicationIdData.one, State.PendingGatekeeperApproval, ActorData.Collaborators.one, None, None, instant)
  val aStateHistoryPendingVerification = StateHistory(ApplicationIdData.one, State.PendingRequesterVerification, ActorData.Collaborators.one, None, None, instant)
  val aStateHistoryPendingRI           = StateHistory(ApplicationIdData.one, State.PendingResponsibleIndividualVerification, ActorData.Collaborators.one, None, None, instant)
}

trait StateHistoryFixtures extends ActorFixtures with ApplicationIdFixtures {
  val aStateHistoryTesting             = StateHistoryData.aStateHistoryTesting
  val aStateHistoryPreProd             = StateHistoryData.aStateHistoryPreProd
  val aStateHistoryProd                = StateHistoryData.aStateHistoryProd
  val aStateHistoryPendingGK           = StateHistoryData.aStateHistoryPendingGK
  val aStateHistoryPendingVerification = StateHistoryData.aStateHistoryPendingVerification
  val aStateHistoryPendingRI           = StateHistoryData.aStateHistoryPendingRI
}
