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

import play.api.libs.json.{JsError, JsSuccess, Reads, Writes}

enum RateLimitTier(val orderIndex: Int):
  case Copper    extends RateLimitTier(7)
  case Bronze    extends RateLimitTier(6)
  case Silver    extends RateLimitTier(5)
  case Gold      extends RateLimitTier(4)
  case Palladium extends RateLimitTier(3)
  case Platinum  extends RateLimitTier(2)
  case Rhodium   extends RateLimitTier(1)

object RateLimitTier {
  given Ordering[RateLimitTier] = Ordering.by(_.orderIndex)

  def apply(text: String): Option[RateLimitTier] = {
    RateLimitTier.values.find(e => e.toString.equalsIgnoreCase(text))
  }

  def unsafeApply(text: String): RateLimitTier = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid Rate Limit Tier"))

  import play.api.libs.json.Reads.*

  given Writes[RateLimitTier] = implicitly[Writes[String]].contramap(_.toString.toUpperCase())

  given Reads[RateLimitTier] = implicitly[Reads[String]].flatMapResult { x =>
    apply(x) match {
      case Some(rlt: RateLimitTier) => JsSuccess(rlt)
      case None                     => JsError(s"Invalid Rate Limit Tier $x")
    }
  }
}
