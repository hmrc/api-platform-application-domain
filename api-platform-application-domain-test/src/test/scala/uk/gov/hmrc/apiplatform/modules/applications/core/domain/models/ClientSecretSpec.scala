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

import java.util.UUID

import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

class ClientSecretSpec extends BaseJsonFormattersSpec with FixedClock {
  import ClientSecretSpec._

  "ClientSecret" should {
    val expectedJsonText = jsonText

    "convert to json" in {
      testToJson(example)(
        "name"       -> s"$clientSecretName",
        "createdOn"  -> s"$nowAsText",
        "id"         -> s"${ClientSecretData.Id.one}",
        "lastAccess" -> s"$nowAsText"
      )
    }

    "read from json" in {
      testFromJson[ClientSecret](expectedJsonText)(example)
    }
  }
}

object ClientSecretSpec extends FixedClock {
  val fakeHashedSecret = "blahblahblah"
  val clientSecretName = UUID.randomUUID.toString.takeRight(4)

  val example = ClientSecret(ClientSecretData.Id.one, clientSecretName, instant, Some(instant))

  val jsonText = s"""{"name":"$clientSecretName","createdOn":"$nowAsText","id":"${ClientSecretData.Id.one}","lastAccess":"${nowAsText}"}"""
}
