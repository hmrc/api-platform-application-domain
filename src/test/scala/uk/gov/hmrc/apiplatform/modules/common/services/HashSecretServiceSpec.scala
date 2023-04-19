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

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class HashSecretServiceSpec extends AnyWordSpec with Matchers {

  "HashSecretService" should {
    val myWorkFactorConfig = new HasWorkFactor {
      val workFactor = 6
    }
    val service = new HashSecretService(myWorkFactorConfig)
    val input = "bob"

    "hash a string" in {
      val output = service.hashSecret(input)

      output shouldNot(be(input))
    }

    "secret can be compared to hashedSecret safely" in {
      val output = service.hashSecret(input)

      service.checkAgainstHash(input, output) shouldBe true
      service.checkAgainstHash("random stuff", output) shouldBe false
    }

    "generate a secret and hash" in {
      val (secret, hash) = service.generateSecretAndHash()

      secret shouldNot(be(hash))
      service.checkAgainstHash(secret, hash) shouldBe true
    }

    "ensure failure in bcrypt is handled" in {
      val (secret, hash) = service.generateSecretAndHash()

      service.checkAgainstHash(secret, "") shouldBe false
    }
  }
}
