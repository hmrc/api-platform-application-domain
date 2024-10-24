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

sealed trait OverrideType {
  def displayText: String = OverrideType.displayText(this)
}

object OverrideType {
  case object PERSIST_LOGIN_AFTER_GRANT      extends OverrideType
  case object GRANT_WITHOUT_TAXPAYER_CONSENT extends OverrideType
  case object SUPPRESS_IV_FOR_AGENTS         extends OverrideType
  case object SUPPRESS_IV_FOR_ORGANISATIONS  extends OverrideType
  case object SUPPRESS_IV_FOR_INDIVIDUALS    extends OverrideType
  case object ORIGIN_OVERRIDE                extends OverrideType

  val values = List(
    PERSIST_LOGIN_AFTER_GRANT,
    GRANT_WITHOUT_TAXPAYER_CONSENT,
    SUPPRESS_IV_FOR_AGENTS,
    SUPPRESS_IV_FOR_ORGANISATIONS,
    SUPPRESS_IV_FOR_INDIVIDUALS,
    ORIGIN_OVERRIDE
  )

  val displayText: OverrideType => String = {
    case PERSIST_LOGIN_AFTER_GRANT      => "Persist login after grant"
    case GRANT_WITHOUT_TAXPAYER_CONSENT => "Grant without taxpayer consent"
    case SUPPRESS_IV_FOR_AGENTS         => "Suppress IV for agents"
    case SUPPRESS_IV_FOR_ORGANISATIONS  => "Suppress IV for organisations"
    case SUPPRESS_IV_FOR_INDIVIDUALS    => "Suppress IV for individuals"
    case ORIGIN_OVERRIDE                => "Origin override"
  }

  def apply(text: String): Option[OverrideType] = OverrideType.values.find(_.toString() == text.toUpperCase)

  def unsafeApply(text: String): OverrideType = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid Override Type"))

  import play.api.libs.json.Format
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.SealedTraitJsonFormatting
  implicit val format: Format[OverrideType] = SealedTraitJsonFormatting.createFormatFor[OverrideType]("Override Type", apply)
}
