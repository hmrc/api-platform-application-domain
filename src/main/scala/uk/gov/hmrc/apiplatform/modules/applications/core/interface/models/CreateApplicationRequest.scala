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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

abstract class CreateApplicationRequest {
  def name: String
  def description: Option[String]
  def collaborators: Set[Collaborator]
  def environment: Environment
  def anySubscriptions: Set[ApiIdentifier]

  def accessType: AccessType
}

object CreateApplicationRequest {
  import play.api.libs.functional.syntax._

  def normaliseEmails(in: Set[Collaborator]): Set[Collaborator] = {
    in.map(c => c.normalise)
  }

  def validateBasics(
      name: String,
      redirectUris: List[RedirectUri],
      collaborators: Set[Collaborator]
    ): Either[Validation, (String, List[RedirectUri], Set[Collaborator])] = {
    for {
      validatedName          <- validateName(name)
      _                      <- validateAdminPresent(collaborators)
      validatedCollaborators <- validateUniqueEmails(collaborators)
      validatedRedirectUris  <- validateRedirectUris(redirectUris)
    } yield (validatedName, validatedRedirectUris, validatedCollaborators)
  }

  implicit val reads =
    CreateApplicationRequestV2.reads.map(_.asInstanceOf[CreateApplicationRequest]) or CreateApplicationRequestV1.reads.map(_.asInstanceOf[CreateApplicationRequest])

  def validateName(name: String): Either[Validation, String] = Either.cond(name.nonEmpty, name, NameIsRequired)

  def validateAdminPresent(collaborators: Set[Collaborator]): Either[Validation, Set[Collaborator]] =
    Either.cond(collaborators.exists(_.isAdministrator), collaborators, AtLeastOneAdministratorIsRequired)

  def validateUniqueEmails(collaborators: Set[Collaborator]): Either[Validation, Set[Collaborator]] = {
    val normalisedCollaborators = normaliseEmails(collaborators)
    val setOfRawEmails          = collaborators.map(_.emailAddress)
    val setOfNormalisedEmails   = normalisedCollaborators.map(_.emailAddress)
    Either.cond(setOfRawEmails.size == setOfNormalisedEmails.size, normalisedCollaborators, EmailsMustbeUnique)
  }

  def validateRedirectUris(redirectUris: List[RedirectUri]): Either[Validation, List[RedirectUri]] =
    Either.cond(redirectUris.size <= 5, redirectUris, NoMoreThanFiveRedirectUrisAllowed)

  sealed trait Validation {
    def errorMessage: String
  }

  case object NameIsRequired extends Validation {
    def errorMessage: String = "Name is required."
  }

  case object AtLeastOneAdministratorIsRequired extends Validation {
    def errorMessage: String = "At least one ADMINISTRATOR collaborator is required."
  }

  case object EmailsMustbeUnique extends Validation {
    def errorMessage: String = "Duplicate email in collaborator."
  }

  case object NoMoreThanFiveRedirectUrisAllowed extends Validation {
    def errorMessage: String = "Maximum number (5) of redirect URIs exceeded"
  }
}