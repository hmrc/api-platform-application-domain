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

enum State:
  /* The order of the following declarations is important since it defines the ordering of the enumeration.
   * Be very careful when changing this, code may be relying on certain values being larger/smaller than others. */
  case Testing, PendingResponsibleIndividualVerification, PendingGatekeeperApproval, PendingRequesterVerification, PreProduction, Production, Deleted

object State {

  extension (s: State) {
    // $COVERAGE-OFF$
    def isPreProduction: Boolean = s == State.PreProduction

    def isProduction: Boolean = s == State.Production

    def isPendingGatekeeperApproval = s == State.PendingGatekeeperApproval

    def isPendingRequesterVerification = s == State.PendingRequesterVerification

    def isDeleted = s == State.Deleted

    def isTesting: Boolean = s == State.Testing

    def isPendingResponsibleIndividualVerification = s == State.PendingResponsibleIndividualVerification

    def isApproved: Boolean = isPreProduction || isProduction

    def isPendingApproval: Boolean = isPendingRequesterVerification || isPendingGatekeeperApproval || isPendingResponsibleIndividualVerification

    def isPendingApprovalOrProduction: Boolean = isPendingRequesterVerification || isPendingResponsibleIndividualVerification || isPendingGatekeeperApproval || isProduction

    def isInTestingOrProduction: Boolean = isTesting || isProduction
    // $COVERAGE-ON$
  }

  def apply(text: String): Option[State] = State.values.find(_.toString.equalsIgnoreCase(text))

  def unsafeApply(text: String): State = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid State"))

  import play.api.libs.json.Format
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.SimpleEnumJsonFormatting
  implicit val format: Format[State] = SimpleEnumJsonFormatting.createEnumFormatFor[State]("State", apply)
}
