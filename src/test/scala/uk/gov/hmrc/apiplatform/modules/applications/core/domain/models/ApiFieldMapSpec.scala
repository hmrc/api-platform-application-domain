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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApiFieldMap

class ApiFieldMapSpec extends HmrcSpec {

  "ApiFieldMap" should {
    "provide an default constructor" in {
      val x: ApiFieldMap[Int] = ApiFieldMap.empty[Int]

      x.isEmpty shouldBe true
    }

    "extract an api from an ApiIdentifier" in {
      import ApiIdentifierData._
      import ApiContextData._
      import ApiVersionNbrData._

      val fieldIntMap = Map(FieldName("a") -> 1, FieldName("b") -> 2)
      val x           = Map(contextA -> Map(versionNbr1 -> fieldIntMap))

      ApiFieldMap.extractApi(identifierA)(x) shouldBe fieldIntMap
      ApiFieldMap.extractApi(identifierB)(x) shouldBe Map.empty
      ApiFieldMap.extractApi(identifierA_1_1)(x) shouldBe Map.empty
    }
  }
}
