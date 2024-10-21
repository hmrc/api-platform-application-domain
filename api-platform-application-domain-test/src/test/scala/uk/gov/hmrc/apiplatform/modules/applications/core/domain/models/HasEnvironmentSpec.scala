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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class HasEnvironmentSpec extends HmrcSpec with Matchers {

  case class TestFixture(deployedTo: Environment) extends HasEnvironment

  "HasEnvironment" should {
    "respond for production" in {
      TestFixture(Environment.PRODUCTION).isProduction shouldBe true
      TestFixture(Environment.PRODUCTION).isSandbox shouldBe false
    }
    "respond for sandbox" in {
      TestFixture(Environment.SANDBOX).isProduction shouldBe false
      TestFixture(Environment.SANDBOX).isSandbox shouldBe true
    }
  }
}
