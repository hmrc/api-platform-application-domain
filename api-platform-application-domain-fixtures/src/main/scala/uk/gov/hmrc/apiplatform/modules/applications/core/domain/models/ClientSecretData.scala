/*
 * Copyright 2024 HM Revenue & Customs
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

import java.time.temporal.ChronoUnit
import java.util.UUID

import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

object ClientSecretData extends FixedClock {

  val one   = ClientSecret(Id.one, UUID.randomUUID.toString, instant.minus(5, ChronoUnit.DAYS), Some(instant))
  val two   = ClientSecret(Id.two, UUID.randomUUID.toString, instant.minus(5, ChronoUnit.DAYS), Some(instant))
  val three = ClientSecret(Id.three, UUID.randomUUID.toString, instant.minus(5, ChronoUnit.DAYS), None)
  val priv  = ClientSecret(Id.priv, UUID.randomUUID.toString, instant.minus(5, ChronoUnit.DAYS), Some(instant))
  val ropc  = ClientSecret(Id.ropc, UUID.randomUUID.toString, instant.minus(5, ChronoUnit.DAYS), Some(instant))

  object Id {
    val one   = ClientSecret.Id.random
    val two   = ClientSecret.Id.random
    val three = ClientSecret.Id.random
    val priv  = ClientSecret.Id.random
    val ropc  = ClientSecret.Id.random
  }
}

trait ClientSecretData {
  val ClientSecretIdOne   = ClientSecretData.Id.one
  val ClientSecretIdTwo   = ClientSecretData.Id.two
  val ClientSecretIdThree = ClientSecretData.Id.three
  val ClientSecretIdPriv  = ClientSecretData.Id.priv
  val ClientSecretIdRopc  = ClientSecretData.Id.ropc

  val ClientSecretOne   = ClientSecretData.one
  val ClientSecretTwo   = ClientSecretData.two
  val ClientSecretThree = ClientSecretData.three
  val ClientSecretPriv  = ClientSecretData.priv
  val ClientSecretRopc  = ClientSecretData.ropc
}
