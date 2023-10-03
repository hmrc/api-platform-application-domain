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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.services

import uk.gov.hmrc.apiplatform.utils.HmrcSpec

import uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models._

class BaseCommandHandlerSpec extends HmrcSpec {
  import cats.implicits._

  "BaseCommandHandler" when {
    object TestMe extends BaseCommandHandler[String]
    val failure: CommandFailure = CommandFailures.ApplicationNotFound

    "using cond-with-failure" should {

      "on passing return unit" in {
        TestMe.cond(true, failure) shouldBe ().validNel[CommandFailure]
      }
      "on failing return the failure" in {
        TestMe.cond(false, failure) shouldBe failure.invalidNel[Boolean]
      }
    }

    "using cond-with-left" should {
      val left: String = "A message"

      "on passing return unit" in {
        TestMe.cond(true, left) shouldBe ().validNel[CommandFailure]
      }
      "on failing return the failure" in {
        TestMe.cond(false, left) shouldBe CommandFailures.GenericFailure(left).invalidNel[Boolean]
      }
    }

    "using cond-with-left-and-right" should {
      val left: CommandFailure = CommandFailures.ApplicationNotFound
      val right: String        = "Yes"

      "on passing return unit" in {
        TestMe.cond(true, left, right) shouldBe right.validNel[CommandFailure]
      }
      "on failing return the failure" in {
        TestMe.cond(false, left, right) shouldBe left.invalidNel[Boolean]
      }
    }

    "mustBeDefined with failure" should {
      "on finding a value return the value" in {
        val value: Option[String] = Some("Test")

        TestMe.mustBeDefined(value, failure) shouldBe value.get.validNel[CommandFailure]
      }

      "on finding a none return the failure" in {
        val value: Option[String] = None
        TestMe.mustBeDefined(value, failure) shouldBe failure.invalidNel[String]
      }
    }

    "mustBeDefined with error message" should {
      val errMsg = "Failed"

      "on finding a value return the value" in {
        val value: Option[String] = Some("Test")

        TestMe.mustBeDefined(value, errMsg) shouldBe value.get.validNel[CommandFailure]
      }

      "on finding a none return the failure" in {
        val value: Option[String] = None
        TestMe.mustBeDefined(value, errMsg) shouldBe CommandFailures.GenericFailure(errMsg).invalidNel[String]
      }
    }
  }
}
