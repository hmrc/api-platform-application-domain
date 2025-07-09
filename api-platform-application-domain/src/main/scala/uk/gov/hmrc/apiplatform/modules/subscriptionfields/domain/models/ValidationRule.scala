/*
 * Copyright 2023 HM Revenue & Customs
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

import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

import org.apache.commons.validator.routines.{DomainValidator, UrlValidator}

import play.api.libs.json._

sealed trait ValidationRule {

  def validate(fieldValue: FieldValue): Boolean = {
    if (fieldValue.isEmpty) true
    else validateAgainstRule(fieldValue)
  }
  protected def validateAgainstRule(fieldValue: FieldValue): Boolean
}

case class RegexValidationRule(regex: String) extends ValidationRule {
  def validateAgainstRule(fieldValue: FieldValue): Boolean = fieldValue.value.matches(regex)
}

object RegexValidationRule {
  implicit val RegexValidationRuleFormat: OFormat[RegexValidationRule] = Json.format[RegexValidationRule]
}

case object UrlValidationRule extends ValidationRule {
  val items                     = ArrayBuffer(new DomainValidator.Item(DomainValidator.ArrayType.GENERIC_PLUS, "mdtp")).asJava
  private val schemes           = Array("http", "https")
  private lazy val urlValidator = new org.apache.commons.validator.routines.UrlValidator(schemes, null, UrlValidator.ALLOW_LOCAL_URLS, DomainValidator.getInstance(true, items))

  def validateAgainstRule(fieldValue: FieldValue): Boolean = {
    urlValidator.isValid(fieldValue.value)
  }
}

object ValidationRule {

  private val ValidationRuleReads: Reads[ValidationRule] = new Reads[ValidationRule] {

    def reads(json: JsValue): JsResult[ValidationRule] = json match {
      case JsObject(fields) if (fields.keys.size == 1) =>
        fields.toList.head match {
          case ("RegexValidationRule", v) => Json.fromJson[RegexValidationRule](v)
          case ("UrlValidationRule", _)   => JsSuccess(UrlValidationRule)
          case (k, v)                     => JsError(s"$k is not a valid validation rule")
        }
      case _                                           => JsError("Cannot read validation rule")
    }
  }

  private val ValidationRuleWrites: Writes[ValidationRule] = new Writes[ValidationRule] {

    def writes(o: ValidationRule): JsValue = o match {
      case r: RegexValidationRule => Json.obj("RegexValidationRule" -> Json.toJson(r))
      case u @ UrlValidationRule  => Json.obj("UrlValidationRule" -> Json.obj())
    }
  }

  implicit val validationRuleFormat: Format[ValidationRule] = Format(ValidationRuleReads, ValidationRuleWrites)
}
