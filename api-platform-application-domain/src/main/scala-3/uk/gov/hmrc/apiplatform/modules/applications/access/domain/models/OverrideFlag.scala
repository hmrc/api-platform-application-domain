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

sealed trait OverrideFlag {
  val overrideType: OverrideType = OverrideFlag.asOverrideType(this)
  override def toString()        = OverrideFlag.show(this)
}

object OverrideFlag {
  case object PersistLogin                                   extends OverrideFlag
  case class SuppressIvForAgents(scopes: Set[String])        extends OverrideFlag
  case class SuppressIvForOrganisations(scopes: Set[String]) extends OverrideFlag
  case class GrantWithoutConsent(scopes: Set[String])        extends OverrideFlag
  case class SuppressIvForIndividuals(scopes: Set[String])   extends OverrideFlag
  case class OriginOverride(origin: String)                  extends OverrideFlag

  def asOverrideType(overrideFlag: OverrideFlag) = overrideFlag match {
    case SuppressIvForAgents(_)        => OverrideType.SuppressIvForAgents
    case SuppressIvForOrganisations(_) => OverrideType.SuppressIvForOrganisations
    case SuppressIvForIndividuals(_)   => OverrideType.SuppressIvForIndividuals
    case GrantWithoutConsent(_)        => OverrideType.GrantWithoutTaxpayerConsent
    case _: PersistLogin.type          => OverrideType.PersistLoginAfterGrant
    case OriginOverride(_)             => OverrideType.OriginOverride
  }

  def show(overrideFlag: OverrideFlag): String = overrideFlag match {
    case SuppressIvForAgents(scopes)        => s"SuppressIvForAgents(${scopes.mkString(", ")})"
    case SuppressIvForOrganisations(scopes) => s"SuppressIvForOrganisations(${scopes.mkString(", ")})"
    case SuppressIvForIndividuals(scopes)   => s"SuppressIvForIndividuals(${scopes.mkString(", ")})"
    case GrantWithoutConsent(scopes)        => s"GrantWithoutConsent(${scopes.mkString(", ")})"
    case _: PersistLogin.type               => "PersistLogin"
    case OriginOverride(origin)             => s"OriginOverride($origin)"
  }

  import play.api.libs.json.*
  import uk.gov.hmrc.play.json.Union
  given OFormat[SuppressIvForAgents]        = Json.format[SuppressIvForAgents]
  given OFormat[SuppressIvForOrganisations] = Json.format[SuppressIvForOrganisations]
  given OFormat[GrantWithoutConsent]        = Json.format[GrantWithoutConsent]
  given OFormat[SuppressIvForIndividuals]   = Json.format[SuppressIvForIndividuals]
  given OFormat[OriginOverride]             = Json.format[OriginOverride]

  import uk.gov.hmrc.apiplatform.modules.common.domain.services.EnumJsonHelper.asScreamingSnakeCase

  given OFormat[OverrideFlag] = Union.from[OverrideFlag]("overrideType")
    .and[GrantWithoutConsent](OverrideType.GrantWithoutTaxpayerConsent.asScreamingSnakeCase)
    .andType(OverrideType.PersistLoginAfterGrant.asScreamingSnakeCase, () => PersistLogin)
    .and[SuppressIvForAgents](OverrideType.SuppressIvForAgents.asScreamingSnakeCase)
    .and[SuppressIvForOrganisations](OverrideType.SuppressIvForOrganisations.asScreamingSnakeCase)
    .and[SuppressIvForIndividuals](OverrideType.SuppressIvForIndividuals.asScreamingSnakeCase)
    .and[OriginOverride](OverrideType.OriginOverride.asScreamingSnakeCase)
    .format
}
