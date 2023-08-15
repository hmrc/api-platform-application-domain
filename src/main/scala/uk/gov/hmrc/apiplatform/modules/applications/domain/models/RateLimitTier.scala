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

package uk.gov.hmrc.apiplatform.modules.applications.domain.models

import scala.collection.immutable.SortedSet

import play.api.libs.json.{JsError, JsSuccess, Reads, Writes}

sealed trait RateLimitTier {
  val orderIndex: Int
}

object RateLimitTier {

  case object BRONZE   extends RateLimitTier { override val orderIndex = 5 }
  case object SILVER   extends RateLimitTier { override val orderIndex = 4 }
  case object GOLD     extends RateLimitTier { override val orderIndex = 3 }
  case object PLATINUM extends RateLimitTier { override val orderIndex = 2 }
  case object RHODIUM  extends RateLimitTier { override val orderIndex = 1 }

  implicit val ordering: Ordering[RateLimitTier] = Ordering.by(_.orderIndex)

  val values: SortedSet[RateLimitTier] = SortedSet[RateLimitTier](RHODIUM, PLATINUM, GOLD, SILVER, BRONZE)

  val orderedForDisplay: Seq[RateLimitTier] = values.toSeq.reverse

  def apply(rateLimitTier: String): Option[RateLimitTier] = {
    RateLimitTier.values.find(e => e.toString == rateLimitTier.toUpperCase)
  }

  def show(rateLimitTier: RateLimitTier): String = rateLimitTier match {
    case BRONZE   => "Bronze"
    case SILVER   => "Silver"
    case GOLD     => "Gold"
    case PLATINUM => "Platinum"
    case RHODIUM  => "Rhodium"
  }

  import play.api.libs.json.Reads._

  implicit val rateLimitTierWrites: Writes[RateLimitTier] = implicitly[Writes[String]].contramap(_.toString)

  implicit val readsRateLimitTier: Reads[RateLimitTier] = implicitly[Reads[String]].flatMapResult { x =>
    apply(x) match {
      case Some(rlt: RateLimitTier) => JsSuccess(rlt)
      case None                     => JsError(s"Invalid rate Limit tier $x")
    }

  }
}
