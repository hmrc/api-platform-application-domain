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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import scala.util.Random

import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class FieldNameSpec extends HmrcSpec {
  val jsonText = """"field1""""

  "JsonFormatter" should {
    "Read raw json" in {
      import play.api.libs.json._
      Json.fromJson[FieldName](Json.parse(jsonText)) shouldBe JsSuccess(FieldName.unsafeApply("field1"))
    }

    "Read bad raw json" in {
      import play.api.libs.json._
      Json.fromJson[FieldName](Json.parse(""" "" """)) shouldBe JsError("FieldName cannot be blank")
    }

    "Write raw json" in {
      import play.api.libs.json._
      Json.toJson[FieldName](FieldNameData.one) shouldBe Json.parse(jsonText)
    }
  }

  "FieldName" should {
    "generate random values" in {
      FieldName.random should not be FieldName.random
    }

    "not allow empty name" in {
      FieldName("") shouldBe None
      intercept[RuntimeException](
        FieldName.unsafeApply("")
      )
    }

    "order" in {
      val values = List("a", "b", "c", "d", "e", "f")

      val fns = values.map(FieldName.unsafeApply(_))
      val rnd = Random.shuffle(fns)

      rnd should not be fns
      rnd.sorted shouldBe fns
    }
  }

}
