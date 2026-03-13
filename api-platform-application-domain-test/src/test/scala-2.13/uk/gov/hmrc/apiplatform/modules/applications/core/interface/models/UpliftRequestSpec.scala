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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.SellResellOrDistributeSpec

class UpliftRequestSpec extends BaseJsonFormattersSpec {
  import UpliftRequestSpec._

  "UpliftRequest" should {
    "convert to json" in {
      Json.toJson[UpliftRequest](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[UpliftRequest](jsonText)(example)
    }
  }
}

object UpliftRequestSpec {

  val example  = UpliftRequest(
    sellResellOrDistribute = SellResellOrDistributeSpec.example,
    subscriptions = Set(ApiIdentifier(ApiContext("context"), ApiVersionNbr("version"))),
    requestedBy = "bob"
  )
  val jsonText = """{"sellResellOrDistribute":"miscblah","subscriptions":[{"context":"context","version":"version"}],"requestedBy":"bob"}"""
}
