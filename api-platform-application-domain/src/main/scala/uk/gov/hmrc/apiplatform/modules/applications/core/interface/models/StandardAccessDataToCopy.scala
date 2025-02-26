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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.{LoginRedirectUri, PostLogoutRedirectUri}

case class StandardAccessDataToCopy(
    redirectUris: List[LoginRedirectUri] = List.empty,
    postLogoutRedirectUris: List[PostLogoutRedirectUri] = List.empty,
    overrides: Set[OverrideFlag] = Set.empty
  ) {

  validate()

  final def validate(): Unit = {
    require(redirectUris.size <= 5, "maximum number of login redirect URIs exceeded")
    require(postLogoutRedirectUris.size <= 5, "maximum number of post logout redirect URIs exceeded")
  }
}

object StandardAccessDataToCopy {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  private val reads: Reads[StandardAccessDataToCopy] = (
    (JsPath \ "redirectUris").read[List[LoginRedirectUri]] and
      ((JsPath \ "postLogoutRedirectUris").readNullable[List[PostLogoutRedirectUri]].map(_.getOrElse(List.empty))) and
      (JsPath \ "overrides").read[Set[OverrideFlag]]
  )(StandardAccessDataToCopy.apply _)

  private val writes: OWrites[StandardAccessDataToCopy]  = Json.writes[StandardAccessDataToCopy]
  implicit val format: OFormat[StandardAccessDataToCopy] = OFormat(reads, writes)
}
