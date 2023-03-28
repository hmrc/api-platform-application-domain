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

package uk.gov.hmrc.apiplatform.modules.common.domain.services

import java.time.Clock
import uk.gov.hmrc.apiplatform.utils.HmrcSpec
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime

class ClockNowSpec extends HmrcSpec {
  import ZoneOffset.UTC

  case class ClockHolder(clock: Clock) extends ClockNow

  "ClockNow" should {
    "provide a now for a clock" in {
      val myInstant = Instant.now()
      val aFixedClock = Clock.fixed(myInstant, UTC)
      val ch = new ClockHolder(aFixedClock)
      val MILLION = 1000 * 1000

      // Should only have complete milliseconds
      ch.now().getNano().%(MILLION) shouldBe 0
      
      ch.now() shouldBe LocalDateTime.ofInstant(myInstant.truncatedTo(ChronoUnit.MILLIS), UTC)
    }

    "truncate a LocalDateTime" in new ClockNow {
      val clock = Clock.systemUTC()

      LocalDateTime.now().withNano(1).truncate().getNano() shouldBe 0
      LocalDateTime.now().withNano(1100100).truncate().getNano() shouldBe 1 * 1000 * 1000
    }

    "provide an instant for a clock" in {
      val myInstant = Instant.now()
      val aFixedClock = Clock.fixed(myInstant, UTC)
      val ch = new ClockHolder(aFixedClock)
      val MILLION = 1000 * 1000

      // Should only have complete milliseconds
      ch.instant().getNano().%(MILLION) shouldBe 0
    }

    "truncate an Instant" in new ClockNow {
      val clock = Clock.systemUTC()
      val ch = new ClockHolder(clock)

      clock.instant().truncate() shouldBe ch.instant()
    }
  }
}
