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

package uk.gov.hmrc.apiplatform.modules.applications.access.domain.models

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.RedirectUriFixtures
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.ImportantSubmissionDataFixtures

object AccessData {

  object Standard {
    val default = Access.Standard()
  }

  object Privileged {
    val default = Access.Privileged()
  }

  object Ropc {
    val default = Access.Ropc()
  }
}

trait AccessFixtures extends RedirectUriFixtures with SellResellOrDistributeFixtures with ImportantSubmissionDataFixtures {
  val stdAccess        = AccessData.Standard.default
  val privilegedAccess = AccessData.Privileged.default
  val ropcAccess       = AccessData.Ropc.default
}
