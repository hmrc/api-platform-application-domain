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

case class FieldDefinition(
    name: FieldName,
    description: String,
    hint: String = "",
    `type`: FieldDefinitionType.FieldDefinitionType,
    shortDescription: String,
    validation: Option[ValidationGroup],
    access: AccessRequirements = AccessRequirements.Default
  )

object FieldDefinition {
  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val readsFieldDefinition: Reads[FieldDefinition] = (
    (JsPath \ "name").read[FieldName] and
      (JsPath \ "description").read[String] and
      ((JsPath \ "hint").read[String] or Reads.pure("")) and
      (JsPath \ "type").read[FieldDefinitionType.Value] and
      ((JsPath \ "shortDescription").read[String] or Reads.pure("")) and
      (JsPath \ "validation").readNullable[ValidationGroup] and
      ((JsPath \ "access").read[AccessRequirements] or Reads.pure(AccessRequirements.Default))
  )(FieldDefinition.apply _)

  implicit val writesFieldDefinition: Writes[FieldDefinition] = new Writes[FieldDefinition] {

    def dropTail[A, B, C, D, E, F, G](t: Tuple7[A, B, C, D, E, F, G]): Tuple6[A, B, C, D, E, F] = (t._1, t._2, t._3, t._4, t._5, t._6)

    // This allows us to hide default AccessRequirements from JSON - as this is a rarely used field
    // but not one that business logic would want as an optional field and require getOrElse everywhere.
    override def writes(o: FieldDefinition): JsValue = {
      val common =
        (JsPath \ "name").write[FieldName] and
          (JsPath \ "description").write[String] and
          (JsPath \ "hint").write[String] and
          (JsPath \ "type").write[FieldDefinitionType.FieldDefinitionType] and
          (JsPath \ "shortDescription").write[String] and
          (JsPath \ "validation").writeNullable[ValidationGroup]

      (if (o.access == AccessRequirements.Default) {
         (common)(unlift(FieldDefinition.unapply).andThen(dropTail))
       } else {
         (common and (JsPath \ "access").write[AccessRequirements])(unlift(FieldDefinition.unapply))
       }).writes(o)
    }
  }

}
