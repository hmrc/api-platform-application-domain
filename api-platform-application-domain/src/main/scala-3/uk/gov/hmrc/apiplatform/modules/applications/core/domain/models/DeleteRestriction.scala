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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import java.time.Instant

import play.api.libs.json.*
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actor

sealed trait DeleteRestriction {
  val deleteRestrictionType: DeleteRestrictionType = DeleteRestriction.deleteRestrictionType(this)
}

object DeleteRestriction {

  private def deleteRestrictionType(deleteRestriction: DeleteRestriction): DeleteRestrictionType = deleteRestriction match {
    case _: DoNotDelete        => DeleteRestrictionType.DoNotDelete
    case _: NoRestriction.type => DeleteRestrictionType.NoRestriction
  }

  case class DoNotDelete(
      reason: String,
      actor: Actor,
      timestamp: Instant
    ) extends DeleteRestriction

  case object NoRestriction extends DeleteRestriction

  import uk.gov.hmrc.play.json.Union
  import Actor.given

  private given OFormat[DoNotDelete] = Json.format[DoNotDelete]

  import uk.gov.hmrc.apiplatform.modules.common.domain.services.EnumJsonHelper.asScreamingSnakeCase

  given OFormat[DeleteRestriction] = Union.from[DeleteRestriction]("deleteRestrictionType")
    .and[DoNotDelete](DeleteRestrictionType.DoNotDelete.asScreamingSnakeCase)
    .andType(DeleteRestrictionType.NoRestriction.asScreamingSnakeCase, () => NoRestriction)
    .format
}
