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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import uk.gov.hmrc.apiplatform.utils.CollaboratorsSyntax._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.LaxEmailAddress.StringSyntax
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiContext, ApiIdentifier, ApiVersionNbr, UserId}
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.{ClientSecret, Collaborator, RedirectUri}
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{PrivacyPolicyLocations, TermsAndConditionsLocations}

trait ApplicationCommandBaseSpec extends HmrcSpec with FixedClock {
  val aCode                       = "aCode"
  val requesterName               = "IAmBobRequester"
  val responsibleIndiviualName    = "IAmAliceTheRI"
  val anActorEmail                = "bob@example.com".toLaxEmail
  val aCollaboratorEmail          = "alice@example.com".toLaxEmail
  val aUserId                     = UserId.random
  val ThreeMillisFourNanos        = 3 * 1000 * 1000 + 4
  val aCollaborator: Collaborator = aCollaboratorEmail.asDeveloper().copy(userId = aUserId)
  val aTimestamp                  = instant
  val reasons                     = "blahblah"

  val aClientSecretId = ClientSecret.Id.random

  val newPrivacyPolicyLocation = PrivacyPolicyLocations.InDesktopSoftware
  val newTandCLocation         = TermsAndConditionsLocations.InDesktopSoftware

  val anApiIdentifier = ApiIdentifier(ApiContext("context"), ApiVersionNbr("version"))

  val aScheduledJob      = "aJobId"
  val anAuthorisationKey = "1234"

  val redirectUri         = RedirectUri("https://someurl.com/path/to/glory")
  val redirectUriToChange = RedirectUri("https://oldUrl/that/needs/a/change")

  val aGatekeeperUser = "Bob in SDST"
}
