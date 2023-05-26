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

package uk.gov.hmrc.apiplatform.modules.common.services

import uk.gov.hmrc.apiplatform.modules.common.domain.services.ClockNow
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.time.Instant
import java.time.Duration

trait FutureTimer {
  self: ClockNow =>
  
  def timeThisFuture[T](f: => Future[T])(implicit ec: ExecutionContext): Future[TimedValue[T]] = {
    println("Starting timer")
    val startTime: Instant = precise()

    f.map( value => {
      println("Mapping")
      val endTime: Instant = precise()
      val duration = Duration.between(startTime, endTime)
      TimedValue(value, duration)
    })
  }
}
