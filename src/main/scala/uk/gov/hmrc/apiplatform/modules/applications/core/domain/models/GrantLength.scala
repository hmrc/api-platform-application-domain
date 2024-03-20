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

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.{DAYS, HOURS}
import scala.collection.immutable.SortedSet

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

/** This class is retained for as long as there are GrantLengthAsInt floating around */

trait GrantLength {
  val duration: Duration

  override def toString() = GrantLength.show(this)
}

object GrantLength {

  private val amountRead: Reads[Long] = (JsPath \ "amount").read[Long]
  private val unitRead: Reads[String] = (JsPath \ "unit").read[String].map(u => u.toUpperCase)

  implicit val durationReads: Reads[Duration] = (
    amountRead and unitRead.map(ChronoUnit.valueOf(_))
  )(Duration.of(_, _))

  implicit val durationWrites: Writes[Duration] = (
    (JsPath \ "amount").write[Long] and
      (JsPath \ "unit").write[String]
  )(d => (d.toSeconds, ChronoUnit.SECONDS.name()))

  implicit val durationFormat: Format[Duration] = Format(durationReads, durationWrites)

  case object FOUR_HOURS extends GrantLength {
    val duration = Duration.of(4, HOURS)
  }

  case object ONE_DAY extends GrantLength {
    val duration = Duration.of(1, DAYS)
  }

  case object ONE_MONTH extends GrantLength {
    val duration = Duration.of(30, DAYS)
  }

  case object THREE_MONTHS extends GrantLength {
    val duration = Duration.of(90, DAYS)
  }

  case object SIX_MONTHS extends GrantLength {
    val duration = Duration.of(180, DAYS)
  }

  case object ONE_YEAR extends GrantLength {
    val duration = Duration.of(365, DAYS)
  }

  case object EIGHTEEN_MONTHS extends GrantLength {
    val duration = Duration.of(547, DAYS)
  }

  case object THREE_YEARS extends GrantLength {
    val duration = Duration.of(1095, DAYS)
  }

  case object FIVE_YEARS extends GrantLength {
    val duration = Duration.of(1825, DAYS)
  }

  case object TEN_YEARS extends GrantLength {
    val duration = Duration.of(3650, DAYS)
  }

  case object ONE_HUNDRED_YEARS extends GrantLength {
    val duration = Duration.of(36500, DAYS)
  }

  implicit val ordering: Ordering[GrantLength] = Ordering.by(_.duration)

  val values = SortedSet[GrantLength](FOUR_HOURS, ONE_DAY, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, EIGHTEEN_MONTHS, THREE_YEARS, FIVE_YEARS, TEN_YEARS, ONE_HUNDRED_YEARS)

  private val allowedIntValues = values.map(_.duration.toDays)

  private val errorMsg: String = "It should only be one of ('4 hours, 1 day', '1 month', '3 months', '6 months', '1 year', '18 months', " +
    "'3 years', '5 years', '10 years', '100 years')"

  def unsafeApply(grantLengthInDays: Int): GrantLength = {
    def fromInt(grantLengthInDays: Int): Option[GrantLength] = {
      GrantLength.values.find(e => e.duration.toDays == grantLengthInDays)
    }

    fromInt(grantLengthInDays).getOrElse(throw new IllegalStateException(s"$grantLengthInDays is not an expected value. $errorMsg"))
  }

  def apply(grantLengthInDays: Int): Option[GrantLength] = {
    GrantLength.values.find(e => e.duration.toDays == grantLengthInDays)
  }

  def apply(amount: Long, unit: String): GrantLength = {
    try {
      val duration = Duration.of(amount, ChronoUnit.valueOf(unit))
      GrantLength.values.find(e => e.duration == duration).getOrElse(throw new IllegalStateException(s"$duration is not an expected value. $errorMsg"))
    } catch {
      case e: IllegalArgumentException if (e.getMessage.contains("No enum constant java.time.temporal.ChronoUnit")) =>
        throw new IllegalStateException(s"$unit is not an expected value for Unit in GrantLength. Must be one of the enum ChronoUnit.")
    }
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

  implicit val writesGrantLength: Writes[GrantLength] = implicitly[Writes[Int]].contramap(x => x.duration.toDays.toInt)

  implicit val readsGrantLength: Reads[GrantLength] = {
    ((JsPath \ "amount").read[Long] and
      (JsPath \ "unit").read[String].map(u => u.toUpperCase)).apply(GrantLength.apply(_, _)).orElse(
      implicitly[Reads[Int]].flatMapResult {
        case i: Int if (allowedIntValues.contains(i)) => JsSuccess(GrantLength.unsafeApply(i))
        case e                                        => JsError(s"Invalid grant length $e")
      }
    )
  }
}
