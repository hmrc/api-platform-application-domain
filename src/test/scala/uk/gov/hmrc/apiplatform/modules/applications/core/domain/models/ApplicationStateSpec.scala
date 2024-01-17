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
        ApplicationState(state, updatedOn = instant).toDeleted(instant)
      }
    }

    "transition to TESTING from any state" in {
      for (state <- State.values) {
        ApplicationState(state, updatedOn = instant).toTesting(instant)
      }
    }

    "transition to PRODUCTION" in {
      ApplicationState(PRE_PRODUCTION, updatedOn = instant).toProduction(instant)
    }

    "fail transistion to PRODUCTION" in {
      for (state <- State.values.filterNot(_ == State.PRE_PRODUCTION)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = instant).toProduction(instant)
        }
      }
    }

    "transition to PRE_PRODUCTION" in {
      ApplicationState(PENDING_REQUESTER_VERIFICATION, updatedOn = instant).toPreProduction(instant)
    }

    "fail transistion to PRE_PRODUCTION" in {
      for (state <- State.values.filterNot(_ == State.PENDING_REQUESTER_VERIFICATION)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = instant).toPreProduction(instant)
        }
      }
    }

    "transition to PENDING_REQUESTER_VERIFICATION" in {
      ApplicationState(PENDING_GATEKEEPER_APPROVAL, updatedOn = instant).toPendingRequesterVerification(instant)
    }

    "fail transistion to PENDING_REQUESTER_VERIFICATION" in {
      for (state <- State.values.filterNot(_ == State.PENDING_GATEKEEPER_APPROVAL)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = instant).toPendingRequesterVerification(instant)
        }
      }
    }

    "transition to PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION" in {
      ApplicationState(TESTING, updatedOn = instant).toPendingResponsibleIndividualVerification(email.text, "Fred Flintstone", instant)
    }

    "fail transistion to PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION" in {
      for (state <- State.values.filterNot(_ == State.TESTING)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = instant).toPendingResponsibleIndividualVerification(email.text, "Fred Flintstone", instant)
        }
      }
    }

    "transition to PENDING_GATEKEEPER_APPROVAL" in {
      ApplicationState(TESTING, updatedOn = instant).toPendingGatekeeperApproval(email.text, "Fred Flintstone", instant)
    }

    "fail transistion to PENDING_GATEKEEPER_APPROVAL" in {
      for (state <- State.values.filterNot(_ == State.TESTING)) {
        intercept[RuntimeException] {
          ApplicationState(state, updatedOn = instant).toPendingGatekeeperApproval(email.text, "Fred Flintstone", instant)
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
  val example  = ApplicationState(State.PRODUCTION, Some(email.text), Some("Fred Flintstone"), None, instant)
  val jsonText = s"""{"name":"PRODUCTION","requestedByEmailAddress":"${email.text}","requestedByName":"Fred Flintstone","updatedOn":"${FixedClock.nowAsText}"}"""
}
