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

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.utils.{BaseJsonFormattersSpec, FixedClock}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._

class ApplicationWithSubscriptionFieldsSpec extends BaseJsonFormattersSpec {
  import ApplicationWithSubscriptionFieldsSpec._

  "ApplicationWithSubscriptionFields" should {
    "convert to json" in {
      Json.toJson[ApplicationWithSubscriptionFields](example) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[ApplicationWithSubscriptionFields](jsonText)(example)
    }
  }
}

object ApplicationWithSubscriptionFieldsSpec extends ApiIdentifierFixture with FixedClock {

  val example = ApplicationWithSubscriptionFields(
    details = CoreApplicationSpec.example,
    collaborators = Set(CollaboratorSpec.Admin.example),
    subscriptions = Set(apiIdentifierOne),
    fieldValues = Map(apiIdentifierOne.context -> Map(apiIdentifierOne.versionNbr -> Map(FieldName("one") -> FieldValue("a"))))
  )

  val jsonText =
    s"""{"details":${CoreApplicationSpec.jsonText},"collaborators":[${CollaboratorSpec.Admin.jsonText}],"subscriptions":[{"context":"${apiIdentifierOne.context}","version":"${apiIdentifierOne.versionNbr}"}],"fieldValues":{"test/contextA":{"1.0":{"one":"a"}}}}"""
}
