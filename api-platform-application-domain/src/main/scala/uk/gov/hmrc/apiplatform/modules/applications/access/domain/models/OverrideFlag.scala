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
    case SuppressIvForAgents(_)        => OverrideType.SUPPRESS_IV_FOR_AGENTS
    case SuppressIvForOrganisations(_) => OverrideType.SUPPRESS_IV_FOR_ORGANISATIONS
    case SuppressIvForIndividuals(_)   => OverrideType.SUPPRESS_IV_FOR_INDIVIDUALS
    case GrantWithoutConsent(_)        => OverrideType.GRANT_WITHOUT_TAXPAYER_CONSENT
    case _: PersistLogin.type          => OverrideType.PERSIST_LOGIN_AFTER_GRANT
    case OriginOverride(_)             => OverrideType.ORIGIN_OVERRIDE
  }

  def show(overrideFlag: OverrideFlag): String = overrideFlag match {
    case SuppressIvForAgents(scopes)        => s"SuppressIvForAgents(${scopes.mkString(", ")})"
    case SuppressIvForOrganisations(scopes) => s"SuppressIvForOrganisations(${scopes.mkString(", ")})"
    case SuppressIvForIndividuals(scopes)   => s"SuppressIvForIndividuals(${scopes.mkString(", ")})"
    case GrantWithoutConsent(scopes)        => s"GrantWithoutConsent(${scopes.mkString(", ")})"
    case _: PersistLogin.type               => "PersistLogin"
    case OriginOverride(origin)             => s"OriginOverride($origin)"
  }

  import play.api.libs.json._
  import uk.gov.hmrc.play.json.Union
  private implicit val formatSuppressIvForAgents: OFormat[SuppressIvForAgents]               = Json.format[SuppressIvForAgents]
  private implicit val formatSuppressIvForOrganisations: OFormat[SuppressIvForOrganisations] = Json.format[SuppressIvForOrganisations]
  private implicit val formatGrantWithoutConsent: OFormat[GrantWithoutConsent]               = Json.format[GrantWithoutConsent]
  private implicit val formatSuppressIvForIndividuals: OFormat[SuppressIvForIndividuals]     = Json.format[SuppressIvForIndividuals]
  private implicit val formatOriginOverride: OFormat[OriginOverride]                         = Json.format[OriginOverride]

  implicit val formatOverride: OFormat[OverrideFlag] = Union.from[OverrideFlag]("overrideType")
    .and[GrantWithoutConsent](OverrideType.GRANT_WITHOUT_TAXPAYER_CONSENT.toString)
    .andType(OverrideType.PERSIST_LOGIN_AFTER_GRANT.toString, () => PersistLogin)
    .and[SuppressIvForAgents](OverrideType.SUPPRESS_IV_FOR_AGENTS.toString)
    .and[SuppressIvForOrganisations](OverrideType.SUPPRESS_IV_FOR_ORGANISATIONS.toString)
    .and[SuppressIvForIndividuals](OverrideType.SUPPRESS_IV_FOR_INDIVIDUALS.toString)
    .and[OriginOverride](OverrideType.ORIGIN_OVERRIDE.toString)
    .format
}
