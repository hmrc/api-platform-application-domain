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

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

class PaginatedApplicationsSpec extends BaseJsonFormattersSpec with PaginatedApplicationsFixtures {
  import PaginatedApplicationsSpec._

  "PaginatedApplications" should {
    "convert to json" in {
      Json.toJson[PaginatedApplications](something) shouldBe Json.parse(jsonText)
    }

    "read from json" in {
      testFromJson[PaginatedApplications](jsonText)(something)
    }

    "get max page size" in {
      PaginatedApplications.maxPage(totalResults = 0, pageSize = 10) shouldBe 0
      PaginatedApplications.maxPage(totalResults = 1, pageSize = 10) shouldBe 1
      PaginatedApplications.maxPage(totalResults = 9, pageSize = 10) shouldBe 1
      PaginatedApplications.maxPage(totalResults = 10, pageSize = 10) shouldBe 1
      PaginatedApplications.maxPage(totalResults = 11, pageSize = 10) shouldBe 2
      PaginatedApplications.maxPage(totalResults = 21, pageSize = 10) shouldBe 3
    }

    "handle page size of 0" in {
      PaginatedApplications.maxPage(totalResults = 0, pageSize = 0) shouldBe 0
    }
  }
}

object PaginatedApplicationsSpec {
  import ApplicationWithCollaboratorsData._

  val applicationsJsonString = Json.toJson(List(standardApp, standardApp2)).toString

  val jsonText =
    s"""{"applications":$applicationsJsonString,"page":1,"pageSize":2,"total":4,"matching":4}"""
}
