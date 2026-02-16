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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApiIdentifierFixtures
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class ApiFieldDefnMapSpec extends BaseJsonFormattersSpec with ApiIdentifierFixtures {

  val subsFieldsDefinitionResponseJsonText =
    s"""{
       |"apis": [
       |    {
       |        "apiContext": "${apiContextOne}",
       |        "apiVersion": "${apiVersionNbrOne}",
       |        "fieldDefinitions": [
       |            {
       |                "name": "callbackUrl",
       |                "description": "What's your callback URL for declaration submissions?",
       |                "hint": "This is how we'll notify you when we've processed them. It must include https and port 443",
       |                "type": "URL",
       |                "shortDescription": "Callback URL",
       |                "validation": {
       |                    "errorMessage": "Callback URL must be a valid URL",
       |                    "rules": [
       |                        {
       |                            "UrlValidationRule": {}
       |                        }
       |                    ]
       |                },
       |                "access": { "devhub" : { "read": "adminOnly", "write" : "noOne" } }
       |            },
       |            {
       |                "name": "securityToken",
       |                "description": "What's the value of the HTTP Authorization header we should use to notify you?",
       |                "hint": "For example: Basic YXNkZnNhZGZzYWRmOlZLdDVOMVhk",
       |                "type": "SecureToken",
       |                "shortDescription": "Security Token",
       |                "validation": {
       |                    "errorMessage": "Security Token must be alphanumeric",
       |                    "rules": [
       |                        {
       |                            "RegexValidationRule": {
       |                                "regex": "^[A-Za-z0-9]+$$"
       |                            }
       |                        }
       |                    ]
       |                }
       |            },
       |            {
       |                "name": "authenticatedEori",
       |                "description": "What's your Economic Operator Registration and Identification (EORI) number?",
       |                "hint": "This is your EORI that will associate your application with you as a CSP",
       |                "type": "STRING",
       |                "shortDescription": "Authenticated Eori",
       |                "validation": {
       |                    "errorMessage": "EORI must be 5 to 10 digits long",
       |                    "rules": [
       |                        {
       |                            "RegexValidationRule": {
       |                                "regex": "^[0-9]{5,10}$$"
       |                            }
       |                        }
       |                    ]
       |                }
       |            }
       |        ]
       |    },
       |    {
       |        "apiContext": "hello",
       |        "apiVersion": "1.0",
       |        "fieldDefinitions": [
       |            {
       |                "name": "helloworldFieldOne",
       |                "description": "What is your name?",
       |                "hint": "You could be Arthur, King of the Britons",
       |                "type": "STRING",
       |                "shortDescription": "Field 1"
       |            },
       |            {
       |                "name": "helloworldFieldTwo",
       |                "description": "What is your quest?",
       |                "hint": "Seeking Holy Grails is a popular pass time",
       |                "type": "STRING",
       |                "shortDescription": "Field 2"
       |            },
       |            {
       |                "name": "helloworldFieldThree",
       |                "description": "What is the airspeed velocity of an unladen swallow?",
       |                "hint": "African Swallow",
       |                "type": "STRING",
       |                "shortDescription": "Field 3"
       |            }
       |        ]
       |    }
       |]
       |}""".stripMargin

  "Parsing the subs fields output" should {
    "read json" in {
      import uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.Implicits.OverrideForBulkResponse._

      val definitions: ApiFieldMap[FieldDefinition] = Json.parse(subsFieldsDefinitionResponseJsonText).as[ApiFieldMap[FieldDefinition]]

      ApiFieldMap.extractApi(apiIdentifierOne)(definitions).get(FieldName("callbackUrl")) shouldBe Symbol("defined")
      ApiFieldMap.extractApi(apiIdentifierOne)(definitions).get(FieldName("unknown")) shouldBe None
    }
  }
}
