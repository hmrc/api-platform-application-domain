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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

case class PostLogoutRedirectUri(uri: String) extends AnyVal {
  override def toString(): String = uri
}

object PostLogoutRedirectUri {
  def apply(uri: String): Option[PostLogoutRedirectUri] = Some(new PostLogoutRedirectUri(uri)).filter(_ => RedirectUri.isValidRedirectUri(uri))

  def unsafeApply(uri: String): PostLogoutRedirectUri =
    apply(uri).fold(throw new IllegalArgumentException(s"Bad format for URI `$uri`"))(identity)

  import play.api.libs.json._

  private val reads: Reads[PostLogoutRedirectUri] = Json.valueReads[PostLogoutRedirectUri].flatMapResult(p =>
    PostLogoutRedirectUri.apply(p.uri).fold[JsResult[PostLogoutRedirectUri]](JsError(s"Bad format for Post logout redirect URI `${p.uri}`"))(u => JsSuccess(u))
  )

  implicit val format: Format[PostLogoutRedirectUri] = Format(reads, Json.valueWrites[PostLogoutRedirectUri])
}
