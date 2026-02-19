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

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util as ju

class InvalidStateTransition(invalidFrom: State, to: State, expectedFrom: State)
    extends RuntimeException(s"Transition to '$to' state requires the application to be in '$expectedFrom' state, but it was in '$invalidFrom'")

case class ApplicationState(
    name: State = State.Testing,
    requestedByEmailAddress: Option[String] = None,
    requestedByName: Option[String] = None,
    verificationCode: Option[String] = None,
    updatedOn: Instant
  ) {

  final def requireState(requirement: State, transitionTo: State): Unit = {
    if (name != requirement) {
      throw new InvalidStateTransition(expectedFrom = requirement, invalidFrom = name, to = transitionTo)
    }
  }

  // $COVERAGE-OFF$
  def isInTesting                                = name.isTesting
  def isPendingResponsibleIndividualVerification = name.isPendingResponsibleIndividualVerification
  def isPendingGatekeeperApproval                = name.isPendingGatekeeperApproval
  def isPendingRequesterVerification             = name.isPendingRequesterVerification
  def isInPreProduction                          = name.isPreProduction
  def isInProduction                             = name.isProduction
  def isDeleted                                  = name.isDeleted
  // $COVERAGE-ON$

  def toProduction(timestamp: Instant) = {
    requireState(requirement = State.PreProduction, transitionTo = State.Production)
    copy(name = State.Production, updatedOn = timestamp)
  }

  def toPreProduction(timestamp: Instant) = {
    requireState(requirement = State.PendingRequesterVerification, transitionTo = State.PreProduction)
    copy(name = State.PreProduction, updatedOn = timestamp)
  }

  def toTesting(timestamp: Instant) = copy(name = State.Testing, requestedByEmailAddress = None, requestedByName = None, verificationCode = None, updatedOn = timestamp)

  def toPendingGatekeeperApproval(requestedByEmailAddress: String, requestedByName: String, timestamp: Instant) = {
    requireState(requirement = State.Testing, transitionTo = State.PendingGatekeeperApproval)

    copy(
      name = State.PendingGatekeeperApproval,
      updatedOn = timestamp,
      requestedByEmailAddress = Some(requestedByEmailAddress),
      requestedByName = Some(requestedByName)
    )
  }

  def toPendingResponsibleIndividualVerification(requestedByEmailAddress: String, requestedByName: String, timestamp: Instant) = {
    requireState(requirement = State.Testing, transitionTo = State.PendingResponsibleIndividualVerification)

    copy(
      name = State.PendingResponsibleIndividualVerification,
      updatedOn = timestamp,
      requestedByEmailAddress = Some(requestedByEmailAddress),
      requestedByName = Some(requestedByName)
    )
  }

  def toPendingRequesterVerification(timestamp: Instant) = {
    requireState(requirement = State.PendingGatekeeperApproval, transitionTo = State.PendingRequesterVerification)

    def verificationCode(input: String = ju.UUID.randomUUID().toString): String = {
      def urlSafe(encoded: String) = encoded.replace("=", "").replace("/", "_").replace("+", "-")

      val digest = MessageDigest.getInstance("SHA-256")
      urlSafe(new String(ju.Base64.getEncoder.encode(digest.digest(input.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8))
    }
    copy(name = State.PendingRequesterVerification, verificationCode = Some(verificationCode()), updatedOn = timestamp)
  }

  def toDeleted(timestamp: Instant) = copy(name = State.Deleted, verificationCode = None, updatedOn = timestamp)
}

object ApplicationState {
  import play.api.libs.json.*

  implicit val formatApplicationState: OFormat[ApplicationState] = Json.format[ApplicationState]
}
