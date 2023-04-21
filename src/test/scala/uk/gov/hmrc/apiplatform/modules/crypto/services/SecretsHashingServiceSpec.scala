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

package uk.gov.hmrc.apiplatform.modules.crypto.services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SecretsHashingServiceSpec extends AnyWordSpec with Matchers {

  "SecretsHashingService" when {
    val service = new SecretsHashingService { val workFactor = 6 }
    val input   = "bob"

    "calling hashSecret" should {
      "hash a string" in {
        val output = service.hashSecret(input)

        output shouldNot be(input)
      }

      "secret can be compared to hashedSecret safely" in {
        val output = service.hashSecret(input)

        service.checkAgainstHash(input, output) shouldBe true
        service.checkAgainstHash("random stuff", output) shouldBe false
      }
    }

    "calling generateSecretAndHash" should {
      "generate a secret and hash" in {
        val (secret, hash) = service.generateSecretAndHash()

        secret shouldNot be(hash)
      }
    }

    "calling checkAgainstHash" should {
      val (secret, hash) = service.generateSecretAndHash()

      "match when appropriate" in {
        service.checkAgainstHash(secret, hash) shouldBe true
      }

      "ensure failure in bcrypt is handled" in {
        service.checkAgainstHash(secret, "") shouldBe false
      }
    }

    "calling requiresRehash" should {
      "with same work factor returns false" in {
        val (secret, hash) = service.generateSecretAndHash()

        service.requiresRehash(hash) shouldBe false
      }

      "with a greater work factor returns true" in {
        val (secret, hash) = service.generateSecretAndHash()
        val newService     = new SecretsHashingService { val workFactor = 8 }

        newService.requiresRehash(hash) shouldBe true

        val rehashed = newService.hashSecret(secret)
        rehashed shouldNot be(hash)

        // Once rehashed it should not require another rehashing
        newService.requiresRehash(rehashed) shouldBe false

        // Downgrade does not force rehash
        service.requiresRehash(rehashed) shouldBe false
      }
    }
  }
}
