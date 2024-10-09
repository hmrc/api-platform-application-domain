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

import uk.gov.hmrc.apiplatform.modules.applications.subscriptions.domain.models._

object ApplicationWithSubscriptionFieldsData {
  val someFieldValues: ApiFieldMap[FieldValue] = Map(ApiContextData.one -> Map(ApiVersionNbrData.one -> Map(FieldNameData.one -> FieldValueData.one)))

  val one = ApplicationWithSubscriptionFields(CoreApplicationData.Standard.one, CollaboratorData.collaborators, ApplicationWithSubscriptionsData.someSubscriptions, someFieldValues)
}

trait ApplicationWithSubscriptionFieldsData extends ApplicationWithSubscriptionsFixtures {
  val someFieldValues = ApplicationWithSubscriptionsData.someSubscriptions

  val appWithSubsFieldsOne = ApplicationWithSubscriptionFieldsData.one

  implicit class ApplicationWithSubscriptionFieldsFixtureSyntax(app: ApplicationWithSubscriptionFields) {
    import monocle.syntax.all._
    def withId(anId: ApplicationId): ApplicationWithSubscriptionFields                           = app.focus(_.details.id).replace(anId)
    def withName(aName: ApplicationName): ApplicationWithSubscriptionFields                      = app.focus(_.details.name).replace(aName)
    def withEnvironment(env: Environment): ApplicationWithSubscriptionFields                     = app.focus(_.details.deployedTo).replace(env)
    def withCollaborators(collabs: Set[Collaborator]): ApplicationWithSubscriptionFields         = app.focus(_.collaborators).replace(collabs)
    def withCollaborators(collabs: Collaborator*): ApplicationWithSubscriptionFields             = withCollaborators(collabs.toSet)
    def withSubscriptions(subs: Set[ApiIdentifier]): ApplicationWithSubscriptionFields           = app.focus(_.subscriptions).replace(subs)
    def withSubscriptions(subs: ApiIdentifier*): ApplicationWithSubscriptionFields               = withSubscriptions(subs.toSet)
    def withFieldValues(fieldValues: ApiFieldMap[FieldValue]): ApplicationWithSubscriptionFields = app.focus(_.fieldValues).replace(fieldValues)
    def withFieldValues(): ApplicationWithSubscriptionFields                                     = withFieldValues(Map.empty)
  }

}
