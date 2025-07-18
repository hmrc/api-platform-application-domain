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

import scala.util.Random

import play.api.libs.json._

case class FieldName private (value: String) extends AnyVal {
  override def toString(): String = value
}

object FieldName {
  val Regex                           = "^[a-zA-Z]+$"
  val Error_Msg                       = "FieldName cannot be blank or contain any characters other than a-z or A-Z"
  def apply(value: String): FieldName = safeApply(value).getOrElse(throw new RuntimeException(Error_Msg))

  def safeApply(value: String): Option[FieldName] = Some(value.trim()).filterNot(_.isEmpty()).filter(_.matches(Regex)).map(new FieldName(_))

  implicit val readsFN: Reads[FieldName]    = Reads.StringReads.flatMapResult(FieldName.safeApply(_).fold[JsResult[FieldName]](JsError(Error_Msg))(f => JsSuccess(f)))
  implicit val writersFN: Writes[FieldName] = Writes.StringWrites.contramap(_.value)

  implicit val keyReadsFieldName: KeyReads[FieldName]   = key => FieldName.safeApply(key).fold[JsResult[FieldName]](JsError(Error_Msg))(f => JsSuccess(f))
  implicit val keyWritesFieldName: KeyWrites[FieldName] = _.value

  implicit val ordering: Ordering[FieldName] = new Ordering[FieldName] {
    override def compare(x: FieldName, y: FieldName): Int = x.value.compareTo(y.value)
  }

  def random = FieldName(Random.alphanumeric.filter(_.isLetter).take(8).mkString) // scalastyle:ignore
}
