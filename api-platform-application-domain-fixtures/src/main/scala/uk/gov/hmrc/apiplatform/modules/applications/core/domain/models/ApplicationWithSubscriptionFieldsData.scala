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

object ApplicationWithSubscriptionFieldsData {
  val someFieldValues: ApiFieldMap[FieldValue] = Map(ApiContextData.one -> Map(ApiVersionNbrData.one -> Map(FieldNameData.one -> FieldValueData.one)))

  val one = ApplicationWithSubscriptionFields(CoreApplicationData.Standard.one, CollaboratorData.collaborators, ApplicationWithSubscriptionsData.someSubscriptions, someFieldValues)
}

trait ApplicationWithSubscriptionFieldsData extends ApplicationWithSubscriptionsFixtures {
  val someFieldValues = ApplicationWithSubscriptionsData.someSubscriptions

  val appWithSubsFieldsOne = ApplicationWithSubscriptionFieldsData.one
}
