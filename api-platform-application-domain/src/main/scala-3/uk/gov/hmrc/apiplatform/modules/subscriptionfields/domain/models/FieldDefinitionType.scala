/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

import play.api.libs.json.*
import uk.gov.hmrc.apiplatform.modules.common.domain.services.SimpleEnumJsonFormatting

enum FieldDefinitionType {
  @deprecated("We don't use URL type for any validation", since = "0.5x") case Url
  case SecureToken, PlainText, PPNSField
}

object FieldDefinitionType {

  extension (fdt: FieldDefinitionType) {
    def label = FieldDefinitionType.labelMe(fdt)
  }

  def apply(text: String): Option[FieldDefinitionType] = FieldDefinitionType.values.find(_.label == text)

  def unsafeApply(text: String): FieldDefinitionType = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid Field Definition Type"))

  private def labelMe(fdt: FieldDefinitionType): String = fdt match {
    case Url         => "URL"
    case SecureToken => "SecureToken"
    case PlainText   => "STRING"
    case PPNSField   => "PPNSField"
  }

  given Format[FieldDefinitionType] = SimpleEnumJsonFormatting.createFormatFor[FieldDefinitionType]("Field Definition Type", apply, label)
}
