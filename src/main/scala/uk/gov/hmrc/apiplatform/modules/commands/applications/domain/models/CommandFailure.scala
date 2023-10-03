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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.play.json.Union

sealed trait CommandFailure {
  override def toString(): String = CommandFailures.describe(this)
}

object CommandFailures {
  case object ApplicationNotFound                    extends CommandFailure
  case class InsufficientPrivileges(details: String) extends CommandFailure
  case object CannotRemoveLastAdmin                  extends CommandFailure
  case object ActorIsNotACollaboratorOnApp           extends CommandFailure
  case object ClientSecretLimitExceeded              extends CommandFailure
  case object CollaboratorDoesNotExistOnApp          extends CommandFailure
  case object CollaboratorHasMismatchOnApp           extends CommandFailure
  case object CollaboratorAlreadyExistsOnApp         extends CommandFailure
  case object DuplicateSubscription                  extends CommandFailure
  case object SubscriptionNotAvailable               extends CommandFailure
  case object NotSubscribedToApi                     extends CommandFailure
  case class GenericFailure(describe: String)        extends CommandFailure

  // $COVERAGE-OFF$ - dont test text output other than the ones that have variable content
  def describe(failure: CommandFailure): String = {
    failure match {
      case _ @ApplicationNotFound            => "Application not found"
      case InsufficientPrivileges(text)      => s"Insufficient privileges - $text"
      case _ @CannotRemoveLastAdmin          => "Cannot remove the last admin from an app"
      case _ @ActorIsNotACollaboratorOnApp   => "Actor is not a collaborator on the app"
      case _ @CollaboratorDoesNotExistOnApp  => "Collaborator does not exist on the app"
      case _ @CollaboratorHasMismatchOnApp   => "Collaborator has mismatched details against the app"
      case _ @CollaboratorAlreadyExistsOnApp => "Collaborator already exists on the app"
      case _ @DuplicateSubscription          => "Duplicate subscription"
      case _ @SubscriptionNotAvailable       => "Subscription not available"
      case _ @NotSubscribedToApi             => "Not subscribed to API"
      case _ @ClientSecretLimitExceeded      => "Client Secrets imit exceeded"
      case GenericFailure(s)                 => s
    }
  }
}
// $COVERAGE-ON$

object CommandFailure {
  import CommandFailures._

  implicit private val formatApplicationNotFound            = Json.format[ApplicationNotFound.type]
  implicit private val formatInsufficientPrivileges         = Json.format[InsufficientPrivileges]
  implicit private val formatCannotRemoveLastAdmin          = Json.format[CannotRemoveLastAdmin.type]
  implicit private val formatActorIsNotACollaboratorOnApp   = Json.format[ActorIsNotACollaboratorOnApp.type]
  implicit private val formatClientSecretLimitExceeded      = Json.format[ClientSecretLimitExceeded.type]
  implicit private val formatCollaboratorDoesNotExistOnApp  = Json.format[CollaboratorDoesNotExistOnApp.type]
  implicit private val formatCollaboratorHasMismatchOnApp   = Json.format[CollaboratorHasMismatchOnApp.type]
  implicit private val formatCollaboratorAlreadyExistsOnApp = Json.format[CollaboratorAlreadyExistsOnApp.type]
  implicit private val formatDuplicateSubscription          = Json.format[DuplicateSubscription.type]
  implicit private val formatSubscriptionNotAvailable       = Json.format[SubscriptionNotAvailable.type]
  implicit private val formatNotSubscribedToApi             = Json.format[NotSubscribedToApi.type]
  implicit private val formatGenericFailure                 = Json.format[GenericFailure]

  implicit val format: Format[CommandFailure] = Union.from[CommandFailure]("failureType")
    .and[ApplicationNotFound.type]("ApplicationNotFound")
    .and[InsufficientPrivileges]("InsufficientPrivileges")
    .and[CannotRemoveLastAdmin.type]("CannotRemoveLastAdmin")
    .and[ActorIsNotACollaboratorOnApp.type]("ActorIsNotACollaboratorOnApp")
    .and[ClientSecretLimitExceeded.type]("ClientSecretLimitExceeded")
    .and[CollaboratorDoesNotExistOnApp.type]("CollaboratorDoesNotExistOnApp")
    .and[CollaboratorHasMismatchOnApp.type]("CollaboratorHasMismatchOnApp")
    .and[CollaboratorAlreadyExistsOnApp.type]("CollaboratorAlreadyExistsOnApp")
    .and[DuplicateSubscription.type]("DuplicateSubscription")
    .and[SubscriptionNotAvailable.type]("SubscriptionNotAvailable")
    .and[NotSubscribedToApi.type]("NotSubscribedToApi")
    .and[GenericFailure]("GenericFailure")
    .format
}
