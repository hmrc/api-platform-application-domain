/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ClientIdFixtures
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

class ApplicationTokenSpec extends BaseJsonFormattersSpec with FixedClock with ClientIdFixtures {
  import ApplicationTokenSpec._

  "ApplicationToken" should {
    val expectedJsonText = jsonText

    "convert to json" in {
      val inner = Json.toJson(ClientSecretSpec.example)
      testToJsonValues(example)(
        "clientId"             -> JsString(clientIdOne.value),
        "accessToken"          -> JsString(anAccessToken),
        "clientSecrets"        -> JsArray(Seq(inner)),
        "lastAccessTokenUsage" -> JsString(nowAsText)
      )
    }

    "convert to json without last access token usage" in {
      val inner = Json.toJson(ClientSecretSpec.example)
      testToJsonValues(NoLastAccess.example)(
        "clientId"      -> JsString(clientIdOne.value),
        "accessToken"   -> JsString(anAccessToken),
        "clientSecrets" -> JsArray(Seq(inner))
      )
    }

    "read from json" in {
      testFromJson[ApplicationToken](expectedJsonText)(example)
    }

    "read from json without last access token usage" in {
      testFromJson[ApplicationToken](NoLastAccess.jsonText)(NoLastAccess.example)
    }
  }
}

object ApplicationTokenSpec extends FixedClock with ClientIdFixtures {
  val anAccessToken = "blahblahblah"

  val example = ApplicationToken(clientIdOne, anAccessToken, List(ClientSecretSpec.example), Some(instant))

  val jsonText = s"""{"clientId":"$clientIdOne","accessToken":"$anAccessToken","clientSecrets":[${ClientSecretSpec.jsonText}],"lastAccessTokenUsage":"$nowAsText"}"""

  object NoLastAccess {
    val example  = ApplicationToken(clientIdOne, anAccessToken, List(ClientSecretSpec.example), None)
    val jsonText = s"""{"clientId":"$clientIdOne","accessToken":"$anAccessToken","clientSecrets":[${ClientSecretSpec.jsonText}]}"""
  }
}
