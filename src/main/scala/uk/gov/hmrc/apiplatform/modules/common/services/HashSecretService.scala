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

import com.github.t3hnar.bcrypt._
import scala.util.{Failure, Success}
import java.util.UUID

trait HasWorkFactor {
  def workFactor: Int
}

class HashSecretService(config: HasWorkFactor) {

  def generateSecretAndHash(): (String, String) = {
    val secret = UUID.randomUUID().toString
    (secret, hashSecret(secret))
  }

  def hashSecret(secret: String): String = secret.bcrypt(config.workFactor)

  def checkAgainstHash(secret: String, hashedSecret: String): Boolean = {
    secret.isBcryptedSafe(hashedSecret) match {
      case Success(result) => result
      case Failure(_)      => false
    }
  }
}


