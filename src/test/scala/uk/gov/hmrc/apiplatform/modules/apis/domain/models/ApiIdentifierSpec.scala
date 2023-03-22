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

package uk.gov.hmrc.apiplatform.modules.apis.domain.models

import org.scalatest.Inspectors

import play.api.libs.json.{JsString, Json}

import uk.gov.hmrc.apiplatform.modules.common.utils.JsonFormattersSpec

class ApiIdentifierSpec extends JsonFormattersSpec with Inspectors {
  val aContext       = ApiContext("c1")
  val aVersion       = ApiVersion("1.0")
  val anApiIdentifer = ApiIdentifier(aContext, aVersion)

  "ApiContext" should {
    "convert to json" in {

      Json.toJson(aContext) shouldBe JsString("c1")
    }

    "read from json" in {
      testFromJson[ApiContext](""""c1"""")(aContext)
    }

    "sort alphabetically" in {
      val c1 = ApiContext("Alice")
      val c2 = ApiContext("Bob")
      val c3 = ApiContext("Cloe")

      List(c3, c1, c2).sorted shouldBe List(c1, c2, c3)
    }

    "generate a random context" in {
      val cs = Range(1, 1000).map(_ => ApiContext.random).map(_.value)
      all(cs) should fullyMatch regex ("[A-Za-z0-9]+")
    }
  }

  "ApiVersion" should {
    "convert to json" in {

      Json.toJson(aVersion) shouldBe JsString("1.0")
    }

    "read from json" in {
      testFromJson[ApiVersion](""""1.0"""")(aVersion)
    }

    "sort numerically" in {
      val v1 = "1.0"
      val v2 = "2.0"
      val v3 = "2.1"

      List(v3, v1, v2).sorted shouldBe List(v1, v2, v3)
    }

    "generate a random version" in {
      val vs = Range(1, 1000).map(_ => ApiVersion.random).map(_.value)
      all(vs) should fullyMatch regex ("[0-9]{1,3}[.][0-9]{1,3}")
    }
  }

  "ApiIdentifier" should {

    "convert to simple text" in {
      anApiIdentifer.asText("--") shouldBe "c1--1.0"
    }

    "convert to json" in {

      testToJson(anApiIdentifer)(("context" -> "c1"), ("version" -> "1.0"))
    }

    "generate a random identifier" in {
      val identifier = ApiIdentifier.random

      identifier.context.value should fullyMatch regex ("[A-Za-z0-9]+")
      identifier.version.value should fullyMatch regex ("[0-9]{1,3}[.][0-9]{1,3}")
    }

    "read from json" in {
      testFromJson[ApiIdentifier]("""{"context":"c1","version":"1.0"}""")(anApiIdentifer)
    }

    "sort according to context and version" in {
      val i1 = ApiIdentifier(ApiContext("alice"), ApiVersion("1.0"))
      val i2 = ApiIdentifier(ApiContext("alice"), ApiVersion("2.0"))
      val i3 = ApiIdentifier(ApiContext("bob"), ApiVersion("1.0"))
      val i4 = ApiIdentifier(ApiContext("bob"), ApiVersion("1.1"))

      List(i3, i1, i4, i2).sorted shouldBe List(i1, i2, i3, i4)
    }
  }
}
