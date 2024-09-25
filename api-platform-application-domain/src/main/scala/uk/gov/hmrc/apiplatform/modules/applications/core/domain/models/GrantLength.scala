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

import java.time.Period
import scala.collection.immutable.SortedSet

import play.api.libs.json.{Reads, _}

/** This class is retained for as long as there are GrantLengthAsInt floating around */

sealed trait GrantLength {
  val period: Period

  override def toString() = GrantLength.show(this)
}

object GrantLength {

  case object FOUR_HOURS extends GrantLength {
    val period = Period.ofDays(0)
  }

  case object ONE_DAY extends GrantLength {
    val period = Period.ofDays(1)
  }

  case object ONE_MONTH extends GrantLength {
    val period = Period.ofDays(30)
  }

  case object THREE_MONTHS extends GrantLength {
    val period = Period.ofDays(90)
  }

  case object SIX_MONTHS extends GrantLength {
    val period = Period.ofDays(180)
  }

  case object ONE_YEAR extends GrantLength {
    val period = Period.ofDays(365)
  }

  case object EIGHTEEN_MONTHS extends GrantLength {
    val period = Period.ofDays(547)
  }

  case object THREE_YEARS extends GrantLength {
    val period = Period.ofDays(1095)
  }

  case object FIVE_YEARS extends GrantLength {
    val period = Period.ofDays(1825)
  }

  case object TEN_YEARS extends GrantLength {
    val period = Period.ofDays(3650)
  }

  case object ONE_HUNDRED_YEARS extends GrantLength {
    val period = Period.ofDays(36500)
  }

  implicit val orderingPeriod: Ordering[Period] = Ordering.by(_.getDays)

  implicit val ordering: Ordering[GrantLength] = Ordering.by(_.period)

  val values = SortedSet[GrantLength](FOUR_HOURS, ONE_DAY, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, EIGHTEEN_MONTHS, THREE_YEARS, FIVE_YEARS, TEN_YEARS, ONE_HUNDRED_YEARS)

  def apply(grantLengthInDays: Int): Option[GrantLength] = {
    GrantLength.values.find(e => e.period.getDays == grantLengthInDays)
  }

  def apply(period: Period): Option[GrantLength] = {
    GrantLength.values.find(possibleValues => possibleValues.period == period)
  }

  def show(grantLength: GrantLength): String = grantLength match {
    case FOUR_HOURS        => "4 hours"
    case ONE_DAY           => "1 day"
    case ONE_MONTH         => "1 month"
    case THREE_MONTHS      => "3 months"
    case SIX_MONTHS        => "6 months"
    case ONE_YEAR          => "1 year"
    case EIGHTEEN_MONTHS   => "18 months"
    case THREE_YEARS       => "3 years"
    case FIVE_YEARS        => "5 years"
    case TEN_YEARS         => "10 years"
    case ONE_HUNDRED_YEARS => "100 years"
  }

  import play.api.libs.json.Reads._

  implicit val writesGrantLength: Writes[GrantLength] = implicitly[Writes[Period]].contramap(x => x.period)

  implicit val readsGrantLength: Reads[GrantLength] = {

    val errorMsg: String = "It should only be one of ('0 days, 1 day', '1 month', '3 months', '6 months', '1 year', '18 months', " +
      "'3 years', '5 years', '10 years', '100 years')"

    def error(period: Period): JsResult[GrantLength]             = JsError(s"$period is not an expected value. $errorMsg")
    def success(grantLength: GrantLength): JsResult[GrantLength] = JsSuccess(grantLength)

    JsPath.read[Period].flatMapResult {
      case p: Period => GrantLength.apply(p).fold(error(p))(success(_))
    }
  }
}
