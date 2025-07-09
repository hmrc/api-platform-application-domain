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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain

import java.{util => ju}

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

package object models {
  type Fields = Map[FieldName, FieldValue]

  type ApiFieldMap[V] = Map[ApiContext, Map[ApiVersionNbr, Map[FieldName, V]]]

  object ApiFieldMap {
    def empty[V]: ApiFieldMap[V] = Map.empty

    def extractApi[V](apiIdentifier: ApiIdentifier)(map: ApiFieldMap[V]): Map[FieldName, V] =
      map
        .getOrElse(apiIdentifier.context, Map.empty)
        .getOrElse(apiIdentifier.versionNbr, Map.empty)
  }

  type FieldErrorMessage = String

  type FieldError = (FieldName, FieldErrorMessage)
  
  type FieldErrorMap = Map[FieldName, FieldErrorMessage]

  object FieldErrorMap {
    val empty = Map.empty[FieldName, FieldErrorMessage]
  }

  private def toApiFieldDefnMap(response: BulkApiFieldDefinitionsResponse): ApiFieldMap[FieldDefinition] = {
    import cats._
    import cats.implicits._
    type MapType = Map[ApiVersionNbr, Map[FieldName, FieldDefinition]]

    // Shortcut combining as we know there will never be records for the same version for the same context
    implicit def monoidVersions: Monoid[MapType] =
      new Monoid[MapType] {
        override def combine(x: MapType, y: MapType): MapType = x ++ y
        override def empty: MapType                           = Map.empty
      }

    Monoid.combineAll(
      response.apis.map(s => Map(s.apiContext -> Map(s.apiVersion -> s.fieldDefinitions.map(fd => fd.name -> fd).toList.toMap)))
    )
  }

  private def toApiFieldValueMap(response: BulkSubscriptionFieldsResponse): ApiFieldMap[FieldValue] = {
    import cats._
    import cats.implicits._
    type MapType = Map[ApiVersionNbr, Map[FieldName, FieldValue]]

    // Shortcut combining as we know there will never be records for the same version for the same context
    implicit def monoidVersions: Monoid[MapType] =
      new Monoid[MapType] {
        override def combine(x: MapType, y: MapType): MapType = x ++ y
        override def empty: MapType                           = Map.empty
      }

    Monoid.combineAll(
      response.subscriptions.map(s => Map(s.apiContext -> Map(s.apiVersion -> s.fields)))
    )
  }

  private implicit val readsSubscriptionFieldsId: Reads[SubscriptionFieldsId] = Json.valueReads[SubscriptionFieldsId]
  private implicit val readsSubscriptionFields: Reads[SubscriptionFields]     = Json.reads[SubscriptionFields]
  private implicit val readsBulkFields: Reads[BulkSubscriptionFieldsResponse] = Json.reads[BulkSubscriptionFieldsResponse]

  private implicit val readsApiFieldDefns: Reads[ApiFieldDefinitions]         = Json.reads[ApiFieldDefinitions]
  private implicit val readsBulkDefns: Reads[BulkApiFieldDefinitionsResponse] = Json.reads[BulkApiFieldDefinitionsResponse]

  object Implicits {
    private val readsApiFieldMapFromBulk: Reads[ApiFieldMap[FieldValue]]           = readsBulkFields.map(toApiFieldValueMap)
    private val readsApiFieldMapDefnsFromBulk: Reads[ApiFieldMap[FieldDefinition]] = readsBulkDefns.map(toApiFieldDefnMap)

    object OverrideForBulkResponse {
      implicit val reads: Reads[ApiFieldMap[FieldValue]]          = readsApiFieldMapFromBulk
      implicit val readsDefn: Reads[ApiFieldMap[FieldDefinition]] = readsApiFieldMapDefnsFromBulk
    }
  }
}

package models {
  private case class SubscriptionFieldsId(value: ju.UUID) extends AnyVal
  private case class SubscriptionFields(clientId: ClientId, apiContext: ApiContext, apiVersion: ApiVersionNbr, fieldsId: SubscriptionFieldsId, fields: Fields)
  private case class BulkSubscriptionFieldsResponse(subscriptions: List[SubscriptionFields])

  private case class ApiFieldDefinitions(apiContext: ApiContext, apiVersion: ApiVersionNbr, fieldDefinitions: List[FieldDefinition])
  private case class BulkApiFieldDefinitionsResponse(apis: List[ApiFieldDefinitions])

}
