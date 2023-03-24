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

package uk.gov.hmrc.apiplatform.modules.applications.domain.models

import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

class ClientSecretSpec extends JsonFormattersSpec with FixedClock {
  val anId = ClientSecret.Id.random
  val fakeHashedSecret = "blahblahblah"
  val aClientSecret = ClientSecret("bob", now, None, anId, fakeHashedSecret)

  "ClientSecret" should {
    val expectedJsonText = s"""{"name":"bob","createdOn":"$nowAsText","id":"${anId.value.toString}","hashedSecret":"$fakeHashedSecret"}"""
    
    "convert to json" in {
      testToJson(aClientSecret)(
        "name" -> "bob",
        "createdOn" -> s"$nowAsText",
        "id" -> s"${anId.value}",
        "hashedSecret" -> s"$fakeHashedSecret"
      )
    }

    "read from json" in {
      testFromJson[ClientSecret](expectedJsonText)(aClientSecret)
    }
  }
}
