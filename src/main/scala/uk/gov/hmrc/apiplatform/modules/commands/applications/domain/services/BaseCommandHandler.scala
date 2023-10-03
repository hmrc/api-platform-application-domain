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

import cats.data.Validated
import cats.implicits._

import uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models._

trait BaseCommandHandler[S] extends CommandHandlerTypes[S] {
  import CommandFailures._

  def cond(cond: => Boolean, left: CommandFailure): Validated[Failures, Unit] = {
    if (cond) ().validNel[CommandFailure] else left.invalidNel[Unit]
  }

  def cond(cond: => Boolean, left: String): Validated[Failures, Unit] = {
    if (cond) ().validNel[CommandFailure] else GenericFailure(left).invalidNel[Unit]
  }

  def cond[R](cond: => Boolean, left: CommandFailure, rValue: R): Validated[Failures, R] = {
    if (cond) rValue.validNel[CommandFailure] else left.invalidNel[R]
  }

  def mustBeDefined[R](value: Option[R], left: CommandFailure): Validated[Failures, R] = {
    value.fold(left.invalidNel[R])(_.validNel[CommandFailure])
  }

  def mustBeDefined[R](value: Option[R], left: String): Validated[Failures, R] = {
    value.fold[Validated[Failures, R]](GenericFailure(left).invalidNel[R])(_.validNel[CommandFailure])
  }
}
