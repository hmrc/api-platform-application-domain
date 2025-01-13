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

trait PaginatedApplicationsFixtures extends ApplicationWithCollaboratorsFixtures {
  private val currentPage        = 1
  private val totalNumberOfPages = 2
  private val numberOfResults    = 4
  private val numberMatching     = 4

  val something = PaginatedApplications(List(standardApp, standardApp2), currentPage, totalNumberOfPages, numberOfResults, numberMatching)
}
