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

enum OverrideType:
  case PersistLoginAfterGrant, GrantWithoutTaxpayerConsent, SuppressIvForAgents, SuppressIvForOrganisations, SuppressIvForIndividuals, OriginOverride

object OverrideType {

  extension (o: OverrideType) {

    def displayText: String = o match {
      case PersistLoginAfterGrant      => "Persist login after grant"
      case GrantWithoutTaxpayerConsent => "Grant without taxpayer consent"
      case SuppressIvForAgents         => "Suppress IV for agents"
      case SuppressIvForOrganisations  => "Suppress IV for organisations"
      case SuppressIvForIndividuals    => "Suppress IV for individuals"
      case OriginOverride              => "Origin override"
    }
  }

  def apply(text: String): Option[OverrideType] = OverrideType.values.find(_.toString().equalsIgnoreCase(text))

  def unsafeApply(text: String): OverrideType = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid Override Type"))

  import play.api.libs.json.Format
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.SimpleEnumJsonFormatting
  given Format[OverrideType] = SimpleEnumJsonFormatting.createEnumFormatFor[OverrideType]("Override Type", apply)
}
