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

import java.util.UUID
import scala.util.{Failure, Success}

import com.github.t3hnar.bcrypt._

abstract class SecretsHashingService() {
  def workFactor: Int

  final def generateSecretAndHash(): (String, String) = {
    val secret = UUID.randomUUID().toString
    (secret, hashSecret(secret))
  }

  final def hashSecret(secret: String): String = secret.bcrypt(workFactor)

  final def checkAgainstHash(secret: String, hashedSecret: String): Boolean = {
    secret.isBcryptedSafe(hashedSecret) match {
      case Success(result) => result
      case Failure(_)      => false
    }
  }

  final def requiresRehash(hashedSecret: String): Boolean = workFactorOfHash(hashedSecret) < workFactor

  final def workFactorOfHash(hashedSecret: String): Int = hashedSecret.split("\\$")(2).toInt
}
