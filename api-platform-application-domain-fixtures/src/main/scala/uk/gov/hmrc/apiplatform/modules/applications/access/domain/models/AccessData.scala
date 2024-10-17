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

package uk.gov.hmrc.apiplatform.modules.applications.access.domain.models

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{ImportantSubmissionData, ImportantSubmissionDataData, ImportantSubmissionDataFixtures, PrivacyPolicyLocation, TermsAndConditionsLocation}

object AccessData {

  object Standard {
    val default = Access.Standard()

    val one = default.copy(
      redirectUris = List(RedirectUriData.one),
      termsAndConditionsUrl = Some("http://localhost:22222/terms"),
      privacyPolicyUrl = Some("http://localhost:22222/privacy")
    )

    val withSubmission = default.copy(
      redirectUris = List(RedirectUriData.one),
      termsAndConditionsUrl = Some("http://localhost:22222/terms"),
      privacyPolicyUrl = Some("http://localhost:22222/privacy"),
      importantSubmissionData = Some(ImportantSubmissionDataData.desktop)
    )
  }

  object Privileged {
    val default = Access.Privileged()
  }

  object Ropc {
    val default = Access.Ropc()
  }
}

trait AccessFixtures extends RedirectUriFixtures with SellResellOrDistributeFixtures with ImportantSubmissionDataFixtures {
  val standardAccess               = AccessData.Standard.default
  val standardAccessOne            = AccessData.Standard.one
  val standardAccessWithSubmission = AccessData.Standard.withSubmission

  val privilegedAccess = AccessData.Privileged.default
  val ropcAccess       = AccessData.Ropc.default

  import monocle.syntax._
  import monocle._

  protected val importantSubmissionDataLens =
    Optional.apply[Access.Standard, ImportantSubmissionData]((a) => a.importantSubmissionData)((i) => (a) => a.copy(importantSubmissionData = Some(i)))

  implicit class AccessDataFixturesSyntax(in: Access.Standard) {
    import monocle.syntax.all._

    private val optic: AppliedOptional[Access.Standard, ImportantSubmissionData] = AppliedPOptional.apply(in, importantSubmissionDataLens)

    def withDesktopSoftware: Access.Standard =
      in
        .focus(_.importantSubmissionData).replace(Some(desktopImportantSubmissionData))
        .focus(_.sellResellOrDistribute).replace(Some(resellYes))

    def withTermsAndConditionsLocation(tnc: TermsAndConditionsLocation): Access.Standard = optic.andThen(termsAndConditionsLocationLens).replace(tnc)
    def withPrivacyPolicyLocation(ppol: PrivacyPolicyLocation): Access.Standard          = optic.andThen(privacyPolicyLocationLens).replace(ppol)
  }
}
