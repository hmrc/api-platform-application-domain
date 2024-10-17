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

import org.scalatest.matchers.should.Matchers

import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.State._

class HasStateSpec extends HmrcSpec with Matchers with FixedClock {
  case class TestFixture(state: ApplicationState) extends HasState

  val allStates: Set[State] =
    Set(TESTING, PENDING_GATEKEEPER_APPROVAL, PENDING_REQUESTER_VERIFICATION, PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION, PRE_PRODUCTION, PRODUCTION, DELETED)

  def buildFixture(state: State): TestFixture = {
    TestFixture(ApplicationState(state, None, None, None, instant))
  }

  "HasState" should {
    "respond to isApproved" in {
      val trueValues  = Set(PRODUCTION, PRE_PRODUCTION)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isApproved shouldBe true)
      falseValues.map(s => buildFixture(s).isApproved shouldBe false)

      // ALIAS
      trueValues.map(s => buildFixture(s).isInPreProductionOrProduction shouldBe true)
      falseValues.map(s => buildFixture(s).isInPreProductionOrProduction shouldBe false)

    }

    "respond to isDeleted" in {
      val trueValues  = List(DELETED)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isDeleted shouldBe true)
      falseValues.map(s => buildFixture(s).isDeleted shouldBe false)
    }

    "respond to isInPendingGatekeeperApprovalOrResponsibleIndividualVerification" in {
      val trueValues  = List(PENDING_GATEKEEPER_APPROVAL, PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isInPendingGatekeeperApprovalOrResponsibleIndividualVerification shouldBe true)
      falseValues.map(s => buildFixture(s).isInPendingGatekeeperApprovalOrResponsibleIndividualVerification shouldBe false)
    }

    "respond to isInPreProduction" in {
      val trueValues  = List(PRE_PRODUCTION)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isInPreProduction shouldBe true)
      falseValues.map(s => buildFixture(s).isInPreProduction shouldBe false)
    }

    "respond to isInProduction" in {
      val trueValues  = List(PRODUCTION)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isInProduction shouldBe true)
      falseValues.map(s => buildFixture(s).isInProduction shouldBe false)
    }

    "respond to isInTesting" in {
      val trueValues  = List(TESTING)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isInTesting shouldBe true)
      falseValues.map(s => buildFixture(s).isInTesting shouldBe false)
    }

    "respond to isPendingGatekeeperApproval" in {
      val trueValues  = List(PENDING_GATEKEEPER_APPROVAL)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isPendingGatekeeperApproval shouldBe true)
      falseValues.map(s => buildFixture(s).isPendingGatekeeperApproval shouldBe false)
    }

    "respond to isPendingRequesterVerification" in {
      val trueValues  = List(PENDING_REQUESTER_VERIFICATION)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isPendingRequesterVerification shouldBe true)
      falseValues.map(s => buildFixture(s).isPendingRequesterVerification shouldBe false)
    }

    "respond to isPendingResponsibleIndividualVerification" in {
      val trueValues  = List(PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION)
      val falseValues = allStates -- trueValues

      trueValues.map(s => buildFixture(s).isPendingResponsibleIndividualVerification shouldBe true)
      falseValues.map(s => buildFixture(s).isPendingResponsibleIndividualVerification shouldBe false)
    }
  }
}
