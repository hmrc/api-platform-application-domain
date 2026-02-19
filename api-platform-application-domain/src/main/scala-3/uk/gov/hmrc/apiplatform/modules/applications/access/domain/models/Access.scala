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

import play.api.libs.json.*

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.*
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.*

sealed trait Access {
  val accessType: AccessType = Access.accessType(this)

  val isStandard = this match {
    case _: Access.Standard => true
    case _                  => false
  }

  val isPriviledged = this match {
    case _: Access.Privileged => true
    case _                    => false
  }

  val isROPC = this match {
    case _: Access.Ropc => true
    case _              => false
  }
}

object Access {

  def accessType(access: Access): AccessType = access match {
    case _: Standard   => AccessType.Standard
    case _: Privileged => AccessType.Privileged
    case _: Ropc       => AccessType.Ropc
  }

  case class Standard(
      redirectUris: List[LoginRedirectUri] = List.empty,
      postLogoutRedirectUris: List[PostLogoutRedirectUri] = List.empty,
      termsAndConditionsUrl: Option[String] = None,
      privacyPolicyUrl: Option[String] = None,
      overrides: Set[OverrideFlag] = Set.empty,
      sellResellOrDistribute: Option[SellResellOrDistribute] = None,
      importantSubmissionData: Option[ImportantSubmissionData] = None
    ) extends Access {

    def privacyPolicyLocation: PrivacyPolicyLocation =
      importantSubmissionData
        .map(_.privacyPolicyLocation)
        .orElse(privacyPolicyUrl.map(PrivacyPolicyLocation.Url(_)))
        .getOrElse(PrivacyPolicyLocation.NoneProvided)

    def termsAndConditionsLocation: TermsAndConditionsLocation =
      importantSubmissionData
        .map(_.termsAndConditionsLocation)
        .orElse(termsAndConditionsUrl.map(TermsAndConditionsLocation.Url(_)))
        .getOrElse(TermsAndConditionsLocation.NoneProvided)
  }

  object Standard {
    import LoginRedirectUri.given
    import PostLogoutRedirectUri.given
    given OFormat[Standard] = Json.format[Standard]
  }

  case class Privileged(
      totpIds: Option[TotpId] = None,
      scopes: Set[String] = Set.empty
    ) extends Access

  case class Ropc(scopes: Set[String] = Set.empty) extends Access

  import uk.gov.hmrc.play.json.Union
  import PostLogoutRedirectUri.given

  private implicit val formatPrivileged: OFormat[Privileged] = Json.format[Privileged]
  private implicit val formatRopc: OFormat[Ropc]             = Json.format[Ropc]

  import uk.gov.hmrc.apiplatform.modules.common.domain.services.EnumJsonHelper.asScreamingSnakeCase

  implicit val format: OFormat[Access] = Union.from[Access]("accessType")
    .and[Standard](AccessType.Standard.asScreamingSnakeCase)
    .and[Privileged](AccessType.Privileged.asScreamingSnakeCase)
    .and[Ropc](AccessType.Ropc.asScreamingSnakeCase)
    .format
}
