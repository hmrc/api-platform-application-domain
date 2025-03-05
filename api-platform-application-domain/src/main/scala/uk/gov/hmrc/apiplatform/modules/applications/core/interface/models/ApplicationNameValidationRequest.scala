package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId

sealed trait ApplicationNameValidationRequest {
  def nameToValidate: String
}

case class ChangeApplicationNameValidationRequest(nameToValidate: String, applicationId: ApplicationId) extends ApplicationNameValidationRequest
case class NewApplicationNameValidationRequest(nameToValidate: String) extends ApplicationNameValidationRequest

object ApplicationNameValidationRequest {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  private val crr: Reads[ChangeApplicationNameValidationRequest] = Json.reads[ChangeApplicationNameValidationRequest]
  private val nrr: Reads[NewApplicationNameValidationRequest] = Json.reads[NewApplicationNameValidationRequest]

  private val reads: Reads[ApplicationNameValidationRequest] = crr.widen[ApplicationNameValidationRequest] or nrr.widen[ApplicationNameValidationRequest]
  
  private val crw = Json.writes[ChangeApplicationNameValidationRequest]
  private val nrw = Json.writes[NewApplicationNameValidationRequest]
  
  private val writes: OWrites[ApplicationNameValidationRequest] = r => r match {
    case c : ChangeApplicationNameValidationRequest => crw.writes(c)
    case n : NewApplicationNameValidationRequest => nrw.writes(n)
  }
  
  implicit val format: Format[ApplicationNameValidationRequest] = Format(reads, writes)
}
