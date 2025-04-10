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

package uk.gov.hmrc.apiplatform.modules.applications.subscriptions.interface.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

import uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models.ApiFieldMapData

object BulkSubscriptionFieldsResponseData {
  val fieldsIdOne: SubscriptionFieldsId = SubscriptionFieldsId.random

  val one = BulkSubscriptionFieldsResponse(
    Seq(
      SubscriptionFields(ClientIdData.one, ApiContextData.one, ApiVersionNbrData.one, fieldsIdOne, ApiFieldMapData.one),
      SubscriptionFields(ClientIdData.one, ApiContextData.one, ApiVersionNbrData.two, fieldsIdOne, ApiFieldMapData.two),
      SubscriptionFields(ClientIdData.one, ApiContextData.two, ApiVersionNbrData.one, fieldsIdOne, ApiFieldMapData.three)
    )
  )
}

trait BulkSubscriptionFieldsResponseFixtures {
  val bulkSubsOne = BulkSubscriptionFieldsResponseData.one

  val emptyBulkSubs = Seq.empty
}
