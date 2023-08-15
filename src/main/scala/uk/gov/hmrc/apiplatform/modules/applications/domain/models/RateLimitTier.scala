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

import play.api.libs.json.{JsError, JsSuccess, Reads, Writes}

import scala.collection.immutable.SortedSet

sealed trait RateLimitTier

object RateLimitTier {

  case object RHODIUM  extends RateLimitTier
  case object PLATINUM extends RateLimitTier
  case object GOLD     extends RateLimitTier
  case object SILVER   extends RateLimitTier
  case object BRONZE   extends RateLimitTier

  implicit val ordering: Ordering[RateLimitTier] = Ordering.by(_.toString)

  val values: SortedSet[RateLimitTier] = SortedSet[RateLimitTier](RHODIUM, PLATINUM, GOLD, SILVER, BRONZE)

  lazy val asOrderedList: List[RateLimitTier] = RateLimitTier.values.toList.sorted

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
      case None => JsError(s"Invalid rate Limit tier $x")
    }
    
  }
}
