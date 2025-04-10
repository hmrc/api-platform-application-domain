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

package uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class ApiFieldMapSpec
    extends HmrcSpec
    with ApiIdentifierFixtures {

  "ApiFieldMap" should {
    "provide an default constructor" in {
      val x: ApiFieldMap[Int] = ApiFieldMap.empty[Int]

      x.isEmpty shouldBe true
    }

    "extract an api from an ApiIdentifier" in {
      val fieldIntMap = Map(
        FieldNameData.one -> 1,
        FieldNameData.two -> 2
      )
      val x           = Map(apiContextOne -> Map(apiVersionNbrOne -> fieldIntMap))

      ApiFieldMap.extractApi(apiIdentifierOne)(x) shouldBe fieldIntMap
      ApiFieldMap.extractApi(apiIdentifierTwo)(x) shouldBe Map.empty
      ApiFieldMap.extractApi(apiIdentifierThree)(x) shouldBe Map.empty
    }
  }
}
