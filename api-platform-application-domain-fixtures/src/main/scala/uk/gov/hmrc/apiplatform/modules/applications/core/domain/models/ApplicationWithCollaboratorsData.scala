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

object ApplicationWithCollaboratorsData {

  val standardApp = ApplicationWithCollaborators(
    CoreApplicationData.Standard.one,
    CollaboratorData.collaborators
  )

  val standardApp2 = ApplicationWithCollaborators(
    CoreApplicationData.Standard.two,
    CollaboratorData.collaborators
  )

  val standardApp3 = ApplicationWithCollaborators(
    CoreApplicationData.Standard.three,
    CollaboratorData.collaborators
  )

  val privilegedApp = ApplicationWithCollaborators(
    CoreApplicationData.Privileged.one,
    CollaboratorData.collaborators
  )

  val ropcApp = ApplicationWithCollaborators(
    CoreApplicationData.Ropc.one,
    CollaboratorData.collaborators
  )
}

trait ApplicationWithCollaboratorsFixtures extends CoreApplicationFixtures with CollaboratorFixtures {
  val standardApp   = ApplicationWithCollaboratorsData.standardApp
  val standardApp2  = ApplicationWithCollaboratorsData.standardApp2
  val standardApp3  = ApplicationWithCollaboratorsData.standardApp3
  val privilegedApp = ApplicationWithCollaboratorsData.privilegedApp
  val ropcApp       = ApplicationWithCollaboratorsData.ropcApp

  implicit class ApplicationWithCollaboratorsFixtureSyntax(app: ApplicationWithCollaborators) {
    import monocle.syntax.all._
    def withId(anId: ApplicationId): ApplicationWithCollaborators               = app.focus(_.details.id).replace(anId)
    def withName(aName: ApplicationName): ApplicationWithCollaborators          = app.focus(_.details.name).replace(aName)
    def withEnvironment(env: Environment): ApplicationWithCollaborators         = app.focus(_.details.deployedTo).replace(env)
    def withCollaborators(collabs: Collaborator*): ApplicationWithCollaborators = app.focus(_.collaborators).replace(collabs.toSet)
  }
}
