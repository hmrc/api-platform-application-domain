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

package uk.gov.hmrc.apiplatform.modules.common.utils

import org.scalatest.matchers.should.Matchers

import play.api.libs.json._

trait BaseJsonFormattersSpec extends HmrcSpec with Matchers {

  def testToJson[T](in: T)(fields: (String, String)*)(implicit wrt: Writes[T]) = {
    val f: Seq[(String, JsValue)] = fields.map { case (k, v) => (k -> JsString(v)) }
    Json.toJson(in) shouldBe JsObject(f)
  }

  def testToJsonValues[T](in: T)(fields: (String, JsValue)*)(implicit wrt: Writes[T]) = {
    Json.toJson(in) shouldBe JsObject(fields)
  }

  def testFromJson[T](text: String)(expected: T)(implicit rdr: Reads[T]) =
    Json.parse(text).validate[T] match {
      case JsSuccess(found, _) if (found == expected) => succeed
      case JsSuccess(found, _)                        => fail(s"Did not get $expected (got $found instead)")
      case JsError(errors)                            => fail(s"Did not succeed ${errors}")
    }

  def testFailJson[T](text: String)(implicit rdr: Reads[T]) =
    Json.parse(text).validate[T] match {
      case JsSuccess(_, _) => fail(s"Should have got a JsError)")
      case JsError(_)      => succeed
    }
}
