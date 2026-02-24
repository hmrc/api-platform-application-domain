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

import org.mindrot.jbcrypt.BCrypt as B

/** This file is almost entirely based on https://github.com/t3hnar/scala-bcrypt/tree/master with it's apache-2 license. We have adapted it here for Scala 3 as the project has not
  * yet published a Scala 3 version and looks to be a dead project. We have chosen to remove the old deprecated methods and rename the bounded methods as the old ones - we have
  * complete control over the users of this code (at least where we provice any certainty to them).
  *
  * @author
  *   Yaroslav Klymko
  */

object BCrypt {
  def gensalt(rounds: Int = 10): String = B.gensalt(rounds)
}

package bcrypt {

  def generateSalt: String = B.gensalt()

  // Maybe consider moving the non deprecated methods no another package with the same method names (loose the "bounded")
  // This way the only change the developers would need to make is change the package
  extension (pswrd: String) {

    def bcrypt: String = {
      if (moreThanLength()) throw illegalArgumentException
      else doBcrypt
    }

    // The default rounds in BCrypt.gensalt() is 10.
    private def doBcrypt: String = B.hashpw(pswrd, BCrypt.gensalt())

    def bcrypt(rounds: Int): String = {
      if (moreThanLength()) throw illegalArgumentException
      else doBcrypt(rounds)
    }

    private def doBcrypt(rounds: Int): String = B.hashpw(pswrd, BCrypt.gensalt(rounds))

    def bcrypt(salt: String): String = {
      if (moreThanLength()) throw illegalArgumentException
      else doBcrypt(salt)
    }

    private def doBcrypt(salt: String): String = B.hashpw(pswrd, salt)

    def isBcrypted(hash: String): Boolean = {
      if (moreThanLength()) throw illegalArgumentException
      else doIsBcrypted(hash)
    }

    private def doIsBcrypted(hash: String): Boolean = B.checkpw(pswrd, hash)

    private def illegalArgumentException = new IllegalArgumentException(s"$pswrd was more than 71 bytes long.")

    private def moreThanLength(length: Int = 71): Boolean = pswrd.length > length
  }
}
