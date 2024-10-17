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

object ApplicationWithSubscriptionsData {
  val someSubscriptions = Set(ApiIdentifierData.one, ApiIdentifierData.two)

  val one = ApplicationWithSubscriptions(CoreApplicationData.Standard.one, CollaboratorData.collaborators, someSubscriptions)
}

trait ApplicationWithSubscriptionsFixtures extends ApplicationWithCollaboratorsFixtures with ApiIdentifierFixtures {
  val someSubscriptions = ApplicationWithSubscriptionsData.someSubscriptions

  val appWithSubsOne = ApplicationWithSubscriptionsData.one

  implicit class ApplicationWithSubscriptionsFixtureSyntax(app: ApplicationWithSubscriptions) {
    import monocle.syntax.all._
    def withId(anId: ApplicationId): ApplicationWithSubscriptions       = app.focus(_.details.id).replace(anId)
    def withName(aName: ApplicationName): ApplicationWithSubscriptions  = app.focus(_.details.name).replace(aName)
    def withEnvironment(env: Environment): ApplicationWithSubscriptions = app.focus(_.details.deployedTo).replace(env)
    def inSandbox(): ApplicationWithSubscriptions                       = app.focus(_.details.deployedTo).replace(Environment.SANDBOX)

    def withCollaborators(collabs: Set[Collaborator]): ApplicationWithSubscriptions = app.focus(_.collaborators).replace(collabs)
    def withCollaborators(collabs: Collaborator*): ApplicationWithSubscriptions     = withCollaborators(collabs.toSet)
    def withSubscriptions(subs: Set[ApiIdentifier]): ApplicationWithSubscriptions   = app.focus(_.subscriptions).replace(subs)
    def withSubscriptions(subs: ApiIdentifier*): ApplicationWithSubscriptions       = withSubscriptions(subs.toSet)
  }
}
