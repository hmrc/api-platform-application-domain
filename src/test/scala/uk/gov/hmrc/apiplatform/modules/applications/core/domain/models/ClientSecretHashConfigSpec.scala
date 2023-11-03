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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import com.typesafe.config.{ConfigException, ConfigFactory}

import uk.gov.hmrc.apiplatform.utils.HmrcSpec

class ClientSecretHashConfigSpec extends HmrcSpec {

  "ClientSecretHashConfig" should {
    "read the reference.conf successfully" in {
      val config = ConfigFactory.load()

      ClientSecretsHashingConfig(config).workFactor shouldBe 6
    }

    "read the config from text" in {
      val myConfig = ConfigFactory.parseString("application-domain-lib.client-secrets-hashing.work-factor : 8")

      ClientSecretsHashingConfig(myConfig).workFactor shouldBe 8
    }

    "fail on bad config" in {
      val myConfig = ConfigFactory.parseString("application-domain-lib.client-secrets-hashing { }")

      intercept[ConfigException] {
        ClientSecretsHashingConfig(myConfig)
      }
    }
  }
}
