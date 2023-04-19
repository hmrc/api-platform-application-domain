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

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ClientSecretHashConfigSpec extends AnyWordSpec with Matchers {

  "ClientSecretHashConfig" should {
    "read the reference.conf successfully" in {
      val config = ConfigFactory.load()

      ClientSecretHashConfig(config).workFactor shouldBe 6
    }

    "read the config from text" in {
      val myConfig = ConfigFactory.parseString("application-domain-lib.client-secret-hashing.work-factor : 8")

      ClientSecretHashConfig(myConfig).workFactor shouldBe 8
    }

    "fail on bad config" in {
      val myConfig = ConfigFactory.parseString("application-domain-lib.client-secret-hashing { }")

      intercept[ConfigException] {
        ClientSecretHashConfig(myConfig)
      }
    }
  }
}
