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

class FieldValueSpec extends BaseJsonFormattersSpec {
  val jsonText = """"value1""""

  "JsonFormatter" should {
    "Read raw json" in {
      testFromJson(jsonText)(FieldValue("value1"))
    }

    "Write raw json" in {
      import play.api.libs.json._
      Json.toJson[FieldValue](FieldValueData.one) shouldBe JsString("value1")
    }

    "generate random values" in {
      FieldValue.random should not be FieldValue.random
    }
  }

  "FieldValue" should {
    "toString" in {
      FieldValue("bob").toString shouldBe "bob"
    }

    "create an empty value" in {
      FieldValue.empty.value shouldBe ""
    }

    "test as empty value" in {
      FieldValue.empty.isEmpty shouldBe true
      FieldValueData.one.isEmpty shouldBe false
    }
  }
}
