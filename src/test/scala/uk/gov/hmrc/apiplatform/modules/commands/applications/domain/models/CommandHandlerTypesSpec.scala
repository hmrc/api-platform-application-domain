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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import cats.data.NonEmptyList

import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class CommandHandlerTypesSpec extends HmrcSpec {
  import CommandFailures._

  "CommandHandlerTypes" should {
    val CHT = new CommandHandlerTypes[String] {}
    import cats.syntax.either._

    "have implicit conversion" in {
      import CHT._
      import CHT.Implicits._

      val expectedResult: Either[Failures, String] = "bob".asRight[Failures]
      Await.result("bob".asSuccess, 1.second) shouldBe expectedResult

      val failure = ApplicationNotFound
      Await.result(failure.asFailure, 1.second) shouldBe Left[Failures, String](NonEmptyList.one(failure))

      val failures = NonEmptyList.of(ApplicationNotFound, CollaboratorDoesNotExistOnApp)
      Await.result(failures.asFailure, 1.second) shouldBe Left[Failures, String](failures)
    }
  }
}
