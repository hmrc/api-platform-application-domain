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

import scala.util.Random

import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class FieldNameSpec extends BaseJsonFormattersSpec {
  val jsonText = """"fieldA""""

  "JsonFormatter" should {
    "Read raw json" in {
      testFromJson(jsonText)(FieldName("fieldA"))
    }

    "Read bad raw json" in {
      testFailJson[FieldName](""" "" """)
    }

    "Write raw json" in {
      import play.api.libs.json._
      Json.toJson(FieldNameData.one) shouldBe JsString("fieldA")
    }
  }

  "FieldName" should {
    "toString" in {
      FieldName("bob").toString shouldBe "bob"
    }

    "generate random values" in {
      uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.FieldName.random should not be uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.FieldName.random
    }

    "allow characters" in {
      uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.FieldName.safeApply("AbcdXYz").value.value shouldBe "AbcdXYz"
    }

    "not allow numbers" in {
      uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.FieldName.safeApply("123") shouldBe None
      intercept[RuntimeException](
        FieldName("123")
      )
    }

    "not allow empty name" in {
      uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.FieldName.safeApply("") shouldBe None
      intercept[RuntimeException](
        FieldName("")
      )
    }

    "order" in {
      val values = List("a", "b", "c", "d", "e", "f")

      val fns = values.map(FieldName(_))
      val rnd = Random.shuffle(fns)

      rnd should not be fns
      rnd.sorted shouldBe fns
    }
  }

}
