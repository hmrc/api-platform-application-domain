/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.prop.TableDrivenPropertyChecks

import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

class ApplicationStateSpec extends BaseJsonFormattersSpec with FixedClock with TableDrivenPropertyChecks {
  import ApplicationStateSpec._

  "ApplicationState" should {
    import State._

    "transition to DELETED from any state" in {
      for (state <- State.values) {
        ApplicationState(state, updatedOn = now()).toDeleted(FixedClock)
      }
    }

    "transition to TESTING from any state" in {
      for (state <- State.values) {
        ApplicationState(state, updatedOn = now()).toTesting(FixedClock)
      }
    }

    "transition to PRODUCTION" in {
      ApplicationState(PRE_PRODUCTION, updatedOn = now()).toProduction(FixedClock)
    }

    "fail transistion to PRODUCTION" in {
      for (state <- State.values.filterNot(_ == State.PRE_PRODUCTION)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = now()).toProduction(FixedClock)
        }
      }
    }

    "transition to PRE_PRODUCTION" in {
      ApplicationState(PENDING_REQUESTER_VERIFICATION, updatedOn = now()).toPreProduction(FixedClock)
    }

    "fail transistion to PRE_PRODUCTION" in {
      for (state <- State.values.filterNot(_ == State.PENDING_REQUESTER_VERIFICATION)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = now()).toPreProduction(FixedClock)
        }
      }
    }

    "transition to PENDING_REQUESTER_VERIFICATION" in {
      ApplicationState(PENDING_GATEKEEPER_APPROVAL, updatedOn = now()).toPendingRequesterVerification(FixedClock)
    }

    "fail transistion to PENDING_REQUESTER_VERIFICATION" in {
      for (state <- State.values.filterNot(_ == State.PENDING_GATEKEEPER_APPROVAL)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = now()).toPendingRequesterVerification(FixedClock)
        }
      }
    }

    "transition to PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION" in {
      ApplicationState(TESTING, updatedOn = now()).toPendingResponsibleIndividualVerification(email.text, "Fred Flintstone", FixedClock)
    }

    "fail transistion to PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION" in {
      for (state <- State.values.filterNot(_ == State.TESTING)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = now()).toPendingResponsibleIndividualVerification(email.text, "Fred Flintstone", FixedClock)
        }
      }
    }

    "transition to PENDING_GATEKEEPER_APPROVAL" in {
      ApplicationState(TESTING, updatedOn = now()).toPendingGatekeeperApproval(email.text, "Fred Flintstone", FixedClock)
    }

    "fail transistion to PENDING_GATEKEEPER_APPROVAL" in {
      for (state <- State.values.filterNot(_ == State.TESTING)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = now()).toPendingGatekeeperApproval(email.text, "Fred Flintstone", FixedClock)
        }
      }
    }

    "produce json" in {
      testToJson[ApplicationState](example)(
        ("name"                    -> "PRODUCTION"),
        ("requestedByEmailAddress" -> email.text),
        ("requestedByName"         -> "Fred Flintstone"),
        ("updatedOn"               -> s"${FixedClock.nowAsText}")
      )
    }

    "read json" in {
      testFromJson[ApplicationState](jsonText)(
        example
      )
    }
  }
}

object ApplicationStateSpec extends FixedClock {
  val email    = LaxEmailAddress("fred@bedrock.com")
  val example  = ApplicationState(State.PRODUCTION, Some(email.text), Some("Fred Flintstone"), None, now())
  val jsonText = s"""{"name":"PRODUCTION","requestedByEmailAddress":"${email.text}","requestedByName":"Fred Flintstone","updatedOn":"${FixedClock.nowAsText}"}"""
}
