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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId

sealed trait ApplicationNameValidationRequest {
  def nameToValidate: String
}

case class ChangeApplicationNameValidationRequest(nameToValidate: String, applicationId: ApplicationId) extends ApplicationNameValidationRequest
case class NewApplicationNameValidationRequest(nameToValidate: String)                                  extends ApplicationNameValidationRequest

object ApplicationNameValidationRequest {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  private val crr: Reads[ChangeApplicationNameValidationRequest] = Json.reads[ChangeApplicationNameValidationRequest]
  private val nrr: Reads[NewApplicationNameValidationRequest]    = Json.reads[NewApplicationNameValidationRequest]

  private val reads: Reads[ApplicationNameValidationRequest] = crr.widen[ApplicationNameValidationRequest] or nrr.widen[ApplicationNameValidationRequest]

  private val crw = Json.writes[ChangeApplicationNameValidationRequest]
  private val nrw = Json.writes[NewApplicationNameValidationRequest]

  private val writes: OWrites[ApplicationNameValidationRequest] = r =>
    r match {
      case c: ChangeApplicationNameValidationRequest => crw.writes(c)
      case n: NewApplicationNameValidationRequest    => nrw.writes(n)
    }

  implicit val format: Format[ApplicationNameValidationRequest] = Format(reads, writes)
}
