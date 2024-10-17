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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.HasEnvironment

class AppLockingSpec extends HmrcSpec with Matchers with FixedClock with ApplicationStateFixtures with TableDrivenPropertyChecks {

  case class TestFixture(deployedTo: Environment, state: ApplicationState) extends HasEnvironment with HasState with AppLocking

  val values = Table(
    ("Environment", "ApplicationState", "Expectation"),
    (Environment.PRODUCTION, appStateTesting, false),
    (Environment.PRODUCTION, appStateProduction, true),
    (Environment.SANDBOX, appStateTesting, false),
    (Environment.SANDBOX, appStateProduction, false)
  )

  "AppLocking" should {
    "return areSubscriptionsLocked as expected" in {
      forAll(values) {
        case (env, state, expectation) => TestFixture(env, state).areSubscriptionsLocked shouldBe expectation
      }
    }
  }
}
