/*
 * Copyright 2026 HM Revenue & Customs
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

import uk.gov.hmrc.apiplatform.modules.crypto.services.bcrypt.*

class bcryptSpec extends AnyWordSpec with Matchers {
  "bounded APIs" should {
    "encrypt, check if bcrypted and fail if bounds are greater than 71 bytes long" in {
      val password     = "my password"
      val hash         = password.bcrypt
      password.isBcrypted(hash) shouldEqual true
      "my new password".isBcrypted(hash) shouldEqual false
      val longPassword = Range(0, 20).map(_ => password).mkString("")
      val cought       = intercept[IllegalArgumentException](longPassword.bcrypt)
      cought.getMessage should be(s"$longPassword was more than 71 bytes long.")
      val cought2      = intercept[IllegalArgumentException](longPassword.isBcrypted(hash))
      cought2.getMessage should be(s"$longPassword was more than 71 bytes long.")
    }

    "encrypt with provided salt and check if bcrypted" in {
      val salt         = BCrypt.gensalt()
      val password     = "password"
      val hash         = password.bcrypt(salt)
      password.isBcrypted(hash) shouldEqual true
      "my new password".isBcrypted(hash) shouldEqual false
      val longPassword = Range(0, 20).map(_ => password).mkString("")
      val cought       = intercept[IllegalArgumentException](longPassword.bcrypt)
      cought.getMessage should be(s"$longPassword was more than 71 bytes long.")
    }

    "encrypt with provided rounds and check if bcrypted" in {
      val password     = "password"
      val hash         = password.bcrypt(10)
      password.isBcrypted(hash) shouldEqual true
      "my new password".isBcrypted(hash) shouldEqual false
      val longPassword = Range(0, 20).map(_ => password).mkString("")
      val cought       = intercept[IllegalArgumentException](longPassword.bcrypt(10))
      cought.getMessage should be(s"$longPassword was more than 71 bytes long.")
    }

    "throw an exception if bcrypt parameters are incorrect" in {
      val invalidSalt = "bad-salt"
      val caught      = intercept[IllegalArgumentException]("password".bcrypt(invalidSalt))
      caught.getMessage shouldEqual "Invalid salt version"
    }
  }
}
