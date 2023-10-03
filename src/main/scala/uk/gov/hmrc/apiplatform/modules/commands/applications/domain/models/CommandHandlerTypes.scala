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

import scala.concurrent.{ExecutionContext, Future}

import cats.data.{EitherT, NonEmptyList}

trait CommandHandlerTypes[S] {

  type Success = S

  type Failures      = NonEmptyList[CommandFailure]
  type AppCmdResult  = Future[Either[Failures, Success]]
  type AppCmdResultT = EitherT[Future, Failures, Success]

  object Implicits {

    implicit class SuccessSyntax(successValue: Success) {
      import cats.syntax.either._
      import cats.syntax.applicative._

      def asSuccess(implicit ec: ExecutionContext): AppCmdResult = successValue.asRight[Failures].pure[Future]
    }

    implicit class FailureSyntax(failureValue: CommandFailure) {
      import cats.syntax.either._
      import cats.syntax.applicative._

      def asFailure(implicit ec: ExecutionContext): AppCmdResult = failureValue.leftNel[Success].pure[Future]
    }

    implicit class FailuresSyntax(failureValues: Failures) {
      import cats.syntax.either._
      import cats.syntax.applicative._

      def asFailure(implicit ec: ExecutionContext): AppCmdResult = failureValues.asLeft[Success].pure[Future]
    }
  }
}
