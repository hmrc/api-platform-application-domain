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

package uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models

object ImportantSubmissionDataData {

  val default = ImportantSubmissionData(
    None,
    ResponsibleIndividualData.one,
    Set(ServerLocation.InUK),
    TermsAndConditionsLocations.NoneProvided,
    PrivacyPolicyLocations.NoneProvided,
    List(TermsOfUseAcceptanceData.one)
  )

  val desktop = ImportantSubmissionData(
    None,
    ResponsibleIndividualData.one,
    Set(ServerLocation.InUK),
    TermsAndConditionsLocations.InDesktopSoftware,
    PrivacyPolicyLocations.InDesktopSoftware,
    List(TermsOfUseAcceptanceData.one)
  )
}

trait ImportantSubmissionDataFixtures extends ResponsibleIndividualFixtures with TermsOfUseAcceptanceFixtures {
  val defaultImportantSubmissionData = ImportantSubmissionDataData.default
  val desktopImportantSubmissionData = ImportantSubmissionDataData.desktop

  import monocle._
  protected val termsAndConditionsLocationLens = Focus[ImportantSubmissionData](_.termsAndConditionsLocation)
  protected val privacyPolicyLocationLens      = Focus[ImportantSubmissionData](_.privacyPolicyLocation)
}
