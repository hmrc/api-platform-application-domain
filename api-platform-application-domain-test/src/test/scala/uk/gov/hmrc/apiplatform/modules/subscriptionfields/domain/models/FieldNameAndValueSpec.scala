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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class FieldNameAndValueSpec extends BaseJsonFormattersSpec {
  val map      = Map(FieldNameData.one -> FieldValue("1"), FieldNameData.two -> FieldValue("2"))
  val jsonText = """{ "field1": "1", "field2": "2" }"""

  "JsonFormatter" should {
    "Read raw map" in {
      testFromJson(jsonText)(map)
    }

    "Write raw map" in {
      testToJson(map)("field1" -> "1", "field2" -> "2")
    }

    "fail on bad map" in {
      val jsonText = """{ "field1": "1", "": "2" }"""
      testFailJson[Map[FieldName, FieldValue]](jsonText)
    }
  }
}
