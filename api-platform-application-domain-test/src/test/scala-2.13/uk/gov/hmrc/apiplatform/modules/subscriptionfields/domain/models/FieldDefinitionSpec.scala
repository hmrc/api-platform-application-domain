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

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class FieldDefinitionSpec extends BaseJsonFormattersSpec with FieldDefinitionFixtures {

  val fieldDefnOneText = s"""{
                            |    "name": "${fieldDefnOne.name}",
                            |    "description": "${fieldDefnOne.description}",
                            |    "hint": "${fieldDefnOne.hint}",
                            |    "type": "${fieldDefnOne.`type`.label}",
                            |    "shortDescription": "${fieldDefnOne.shortDescription}"
                            |}""".stripMargin

  val fieldDefnTwoText = s"""{
                            |    "name": "${fieldDefnTwo.name}",
                            |    "description": "${fieldDefnTwo.description}",
                            |    "hint": "${fieldDefnTwo.hint}",
                            |    "type": "${fieldDefnTwo.`type`.label}",
                            |    "shortDescription": "${fieldDefnTwo.shortDescription}",
                            |    "access": {"devhub":{"read":"adminOnly","write":"adminOnly"}}
                            |}""".stripMargin

  val fieldDefnOneAltText = s"""{
                               |    "name": "${fieldDefnOne.name}",
                               |    "description": "${fieldDefnOne.description}",
                               |    "hint": "${fieldDefnOne.hint}",
                               |    "type": "${fieldDefnOne.`type`.label}",
                               |    "shortDescription": "${fieldDefnOne.shortDescription}",
                               |    "validation": {
                               |        "errorMessage": "Bang",
                               |        "rules": [
                               |            {
                               |                "RegexValidationRule": {
                               |                    "regex": ".*"
                               |                }
                               |            }
                               |        ]
                               |    }
                               |}""".stripMargin

  "JsonFormatter" should {
    "Read raw json without access or validation" in {
      testFromJson[FieldDefinition](fieldDefnOneText)(fieldDefnOne)
    }

    "Read raw json without access" in {
      testFromJson[FieldDefinition](fieldDefnOneAltText)(fieldDefnOne.copy(validation = Some(groupOne)))
    }

    "Read raw json with access" in {
      testFromJson[FieldDefinition](fieldDefnTwoText)(fieldDefnTwo)
    }

    "Read bad raw json" in {
      testFailJson[FieldDefinition](""" "" """)
    }

    "Read bad json" in {
      testFailJson[FieldDefinition]("""{"eh":"bang"}""")
    }

    "Write raw json" in {
      Json.toJson[FieldDefinition](fieldDefnTwo).toString shouldBe Json.parse(fieldDefnTwoText).toString
    }
  }
}
