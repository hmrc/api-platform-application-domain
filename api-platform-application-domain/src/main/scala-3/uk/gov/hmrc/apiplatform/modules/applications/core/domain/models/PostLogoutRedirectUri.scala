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

opaque type PostLogoutRedirectUri <: String = String

object PostLogoutRedirectUri {

  extension (s: PostLogoutRedirectUri) {
    def uri: String = s
  }

  def apply(uri: String): Option[PostLogoutRedirectUri] = Some(uri).filter(_ => RedirectUri.isValidRedirectUri(uri))

  def unsafeApply(uri: String): PostLogoutRedirectUri =
    apply(uri).fold(throw new IllegalArgumentException(s"Bad format for URI `$uri`"))(identity)

  import play.api.libs.json.*

  private val reads: Reads[PostLogoutRedirectUri] = Reads.StringReads.flatMapResult(s =>
    PostLogoutRedirectUri.apply(s).fold[JsResult[PostLogoutRedirectUri]](JsError(s"Bad format for Post logout redirect URI `${s}`"))(u => JsSuccess(u))
  )

  given Format[PostLogoutRedirectUri] = Format(reads, Writes.StringWrites)
}
