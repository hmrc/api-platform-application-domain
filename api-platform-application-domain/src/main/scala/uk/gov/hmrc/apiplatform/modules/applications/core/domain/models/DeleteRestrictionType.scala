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

sealed trait DeleteRestrictionType {
  val displayText: String = this.toString().toLowerCase().capitalize
}

object DeleteRestrictionType {
  case object DO_NOT_DELETE  extends DeleteRestrictionType
  case object NO_RESTRICTION extends DeleteRestrictionType

  val values = List(DO_NOT_DELETE, NO_RESTRICTION)

  def apply(text: String): Option[DeleteRestrictionType] = DeleteRestrictionType.values.find(_.toString() == text.toUpperCase)

  def unsafeApply(text: String): DeleteRestrictionType = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid Delete Restriction Type"))

  import play.api.libs.json.Format
  import uk.gov.hmrc.apiplatform.modules.common.domain.services.SealedTraitJsonFormatting
  implicit val format: Format[DeleteRestrictionType] = SealedTraitJsonFormatting.createFormatFor[DeleteRestrictionType]("Delete Restriction Type", apply)

}
