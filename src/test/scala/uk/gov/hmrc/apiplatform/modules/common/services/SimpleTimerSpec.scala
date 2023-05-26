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

class SimpleTimerSpec extends AnyWordSpec with Matchers with Inside {

  class FakedClockTimer(instants: Queue[Instant]) extends ClockWithInstants(instants) with SimpleTimer

  "SimpleTimer" should {
    "capture a duration" in {
      val fakedClockTimer = new FakedClockTimer(Queue(FixedClock.instant, FixedClock.instant.plusNanos(10000)))

      inside(fakedClockTimer.timeThis(() => true)) { case TimedValue(value, duration) =>
        duration.getSeconds() shouldBe 0
        duration.getNano() shouldBe 10000
      }
    }
  }
}
