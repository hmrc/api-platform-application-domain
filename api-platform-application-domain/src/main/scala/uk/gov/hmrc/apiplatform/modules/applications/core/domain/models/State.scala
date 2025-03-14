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

import scala.collection.immutable.ListSet

sealed trait State {
  // $COVERAGE-OFF$
  val isPreProduction: Boolean = this == State.PRE_PRODUCTION

  val isProduction: Boolean = this == State.PRODUCTION

  val isPendingGatekeeperApproval = this == State.PENDING_GATEKEEPER_APPROVAL

  val isPendingRequesterVerification = this == State.PENDING_REQUESTER_VERIFICATION

  val isDeleted = this == State.DELETED

  val isTesting: Boolean = this == State.TESTING

  val isPendingResponsibleIndividualVerification = this == State.PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION

  val isApproved: Boolean = isPreProduction || isProduction

  val isPendingApproval: Boolean = isPendingRequesterVerification || isPendingGatekeeperApproval || isPendingResponsibleIndividualVerification

  val isPendingApprovalOrProduction: Boolean = isPendingRequesterVerification || isPendingResponsibleIndividualVerification || isPendingGatekeeperApproval || isProduction

  val isInTestingOrProduction: Boolean = isTesting || isProduction
  // $COVERAGE-ON$
}

object State {

  case object TESTING                                     extends State
  case object PENDING_GATEKEEPER_APPROVAL                 extends State
  case object PENDING_REQUESTER_VERIFICATION              extends State
  case object PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION extends State
  case object PRE_PRODUCTION                              extends State
  case object PRODUCTION                                  extends State
  case object DELETED                                     extends State

  /* The order of the following declarations is important since it defines the ordering of the enumeration.
   * Be very careful when changing this, code may be relying on certain values being larger/smaller than others. */
  val values = ListSet(TESTING, PENDING_RESPONSIBLE_INDIVIDUAL_VERIFICATION, PENDING_GATEKEEPER_APPROVAL, PENDING_REQUESTER_VERIFICATION, PRE_PRODUCTION, PRODUCTION, DELETED)

  def apply(text: String): Option[State] = State.values.find(_.toString.toUpperCase == text.toUpperCase())

  def unsafeApply(text: String): State = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid State"))

  import play.api.libs.json.Format
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.SealedTraitJsonFormatting
  implicit val format: Format[State] = SealedTraitJsonFormatting.createFormatFor[State]("State", apply)
}
