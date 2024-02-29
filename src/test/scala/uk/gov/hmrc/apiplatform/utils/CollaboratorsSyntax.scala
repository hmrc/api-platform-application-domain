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

package uk.gov.hmrc.apiplatform.utils

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{LaxEmailAddress, UserId}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.Collaborators

trait CollaboratorsSyntax {

  implicit class CollaboratorSyntax(email: LaxEmailAddress) {
    def asDeveloper()     = Collaborators.Developer(userId = UserId.random, emailAddress = email)
    def asAdministrator() = Collaborators.Administrator(userId = UserId.random, emailAddress = email)
  }
}

object CollaboratorsSyntax extends CollaboratorsSyntax
