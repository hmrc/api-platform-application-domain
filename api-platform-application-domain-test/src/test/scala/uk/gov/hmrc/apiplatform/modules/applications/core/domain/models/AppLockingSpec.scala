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
import org.scalatest.prop.TableDrivenPropertyChecks

import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.{Access, AccessFixtures}

class AppLockingSpec extends HmrcSpec with Matchers with FixedClock with ApplicationStateFixtures with AccessFixtures with TableDrivenPropertyChecks {

  case class TestFixture(deployedTo: Environment, state: ApplicationState, access: Access) extends HasEnvironment with HasState with HasAccess with AppLocking

  val values = Table(
    ("Environment", "ApplicationState", "Access", "Expectation"),
    (Environment.PRODUCTION, appStateTesting, standardAccess, false),
    (Environment.PRODUCTION, appStateTesting, privilegedAccess, true),
    (Environment.PRODUCTION, appStateTesting, ropcAccess, true),
    (Environment.PRODUCTION, appStateProduction, standardAccess, true),
    (Environment.SANDBOX, appStateTesting, standardAccess, false),
    (Environment.SANDBOX, appStateProduction, standardAccess, false),
    (Environment.SANDBOX, appStateProduction, privilegedAccess, true),
    (Environment.SANDBOX, appStateProduction, ropcAccess, true)
  )

  "AppLocking" should {
    "return areSubscriptionsLocked as expected" in {
      forAll(values) {
        case (env, state, access, expectation) => TestFixture(env, state, access).areSubscriptionsLocked shouldBe expectation
      }
    }
  }
}
