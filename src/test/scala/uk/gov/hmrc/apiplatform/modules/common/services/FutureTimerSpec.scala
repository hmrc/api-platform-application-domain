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

import java.time.Instant
import scala.collection.mutable.Queue

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock
import org.scalatest.Inside
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import uk.gov.hmrc.apiplatform.modules.common.domain.services.ClockNow
import java.time.Clock
import java.time.ZoneOffset

class FutureTimerSpec extends AnyWordSpec with Matchers with Inside {

  class FakedClockTimer(instants: Queue[Instant]) extends ClockWithInstants(instants) with FutureTimer

  "FutureTimer" should {
    "capture a fake duration" in {
      val fakedClockTimer = new FakedClockTimer(Queue(FixedClock.instant, FixedClock.instant.plusMillis(20000)))

      lazy val makeDelayedFuture = Future {
        Thread.sleep(1000)
        1
      }

      inside(Await.result(fakedClockTimer.timeThisFuture(makeDelayedFuture), 5.seconds)) { case TimedValue(value, timedDuration) =>
        value shouldBe 1
        timedDuration.getSeconds() shouldBe 20
      }
    }

    "capture a real duration" in {
      val timer = new FutureTimer with ClockNow { def clock = Clock.system(ZoneOffset.UTC)}

      lazy val makeDelayedFuture = Future {
        println("Running future")
        Thread.sleep(2100)
        55
      }

      inside(Await.result(timer.timeThisFuture(makeDelayedFuture), 5.seconds)) { case TimedValue(value, timedDuration) =>
        value shouldBe 55
        timedDuration.getSeconds shouldBe >=(2L)
      }
    }
  }
}
