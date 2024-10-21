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

package uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models

import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class FieldValueSpec extends HmrcSpec {
  val jsonText = """"value1""""

  "JsonFormatter" should {
    "Read raw json" in {
      import play.api.libs.json._
      Json.fromJson[FieldValue](Json.parse(jsonText)) shouldBe JsSuccess(FieldValue("value1"))
    }

    "Write raw json" in {
      import play.api.libs.json._
      Json.toJson[FieldValue](FieldValueData.one) shouldBe Json.parse(jsonText)
    }

    "generate random values" in {
      uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models.FieldValue.random should not be uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models.FieldValue.random
    }
  }

  "FieldValue" should {
    "create an empty value" in {
      uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models.FieldValue.empty.value shouldBe ""
    }

    "test as empty value" in {
      uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models.FieldValue.empty.isEmpty shouldBe true
      FieldValueData.one.isEmpty shouldBe false
    }
  }
}
