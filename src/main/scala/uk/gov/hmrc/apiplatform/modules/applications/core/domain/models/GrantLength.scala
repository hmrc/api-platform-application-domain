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

import cats.implicits.toFunctorOps
import play.api.libs.functional.syntax.toFunctionalBuilderOps

import scala.collection.immutable.SortedSet
import play.api.libs.json.{Reads, _}
import play.api.libs.functional.syntax._
import play.api.libs.json.JsPath.\
import play.api.libs.json.Reads._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{DAYS, FiniteDuration, HOURS}
import scala.concurrent.duration.FiniteDuration._

/** This class is retained for as long as there are GrantLengthAsInt floating around */
final case class GrantLengthAsInt(value: Int) extends AnyVal {
  override def toString() = value.toString

  /** This method should only be used when reading */
  def asFiniteDuration: FiniteDuration = FiniteDuration(value, TimeUnit.DAYS)
}

object GrantLengthAsInt {
  implicit val formatGrantLengthAsInt: Format[GrantLengthAsInt] = Json.valueFormat[GrantLengthAsInt]
}

sealed trait GrantLength {
  val duration: FiniteDuration

  override def toString() = GrantLength.show(this)
}

object GrantLength {

  val l: Reads[Long] = (JsPath \ "length").read[Long]
  val u: Reads[String] = (JsPath \ "unit").read[String]

  implicit val finiteDurationReads: Reads[FiniteDuration] = (
    l and u
    )(FiniteDuration.apply (_,_))

  implicit val finiteDurationWrites: Writes[FiniteDuration] = (
    (JsPath \ "length").write[Long] and
      (JsPath \ "unit").write[String]
    )(l => (l.length, l.unit.toString))

  implicit val finiteDurationFormat: Format[FiniteDuration] = Format(finiteDurationReads, finiteDurationWrites)

  case object FOUR_HOURS extends GrantLength {
    val duration = FiniteDuration(4, HOURS)
  }

  case object ONE_DAY extends GrantLength {
    val duration = FiniteDuration(1, DAYS)
  }

  case object ONE_MONTH extends GrantLength {
    val duration = FiniteDuration(30, DAYS)
  }

  case object THREE_MONTHS extends GrantLength {
    val duration = FiniteDuration(90, DAYS)
  }

  case object SIX_MONTHS extends GrantLength {
    val duration = FiniteDuration(180, DAYS)
  }

  case object ONE_YEAR extends GrantLength {
    val duration = FiniteDuration(365, DAYS)
  }

  case object EIGHTEEN_MONTHS extends GrantLength {
    val duration = FiniteDuration(547, DAYS)
  }

  case object THREE_YEARS extends GrantLength {
    val duration = FiniteDuration(1095, DAYS)
  }

  case object FIVE_YEARS extends GrantLength {
    val duration = FiniteDuration(1825, DAYS)
  }

  case object TEN_YEARS extends GrantLength {
    val duration = FiniteDuration(3650, DAYS)
  }

  case object ONE_HUNDRED_YEARS extends GrantLength {
    val duration = FiniteDuration(36500, DAYS)
  }

  implicit val ordering: Ordering[GrantLength] = Ordering.by(_.duration)
  val values = SortedSet[GrantLength](FOUR_HOURS, ONE_DAY, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, EIGHTEEN_MONTHS, THREE_YEARS, FIVE_YEARS, TEN_YEARS, ONE_HUNDRED_YEARS)

  private val allowedValues = values.map(_.duration)
  private val allowedIntValues = values.map(_.duration.toDays)
  private val errorMsg: String = "It should only be one of ('4 hours, 1 day', '1 month', '3 months', '6 months', '1 year', '18 months', " +
    "'3 years', '5 years', '10 years', '100 years')"

  def asDuration(days: Int): FiniteDuration = FiniteDuration(days, TimeUnit.DAYS)
  def unsafeApply(grantLengthInDays: Int): GrantLength = {
    fromInt(grantLengthInDays).getOrElse(throw new IllegalStateException(s"$grantLengthInDays is not an expected value. $errorMsg"))
  }

  def fromInt(grantLengthInDays: Int): Option[GrantLength] = {
    GrantLength.values.find(e => e.duration.toDays == grantLengthInDays)
  }

  def apply(length: Long, unit: String): GrantLength = {

      val duration = FiniteDuration(length, unit)
      GrantLength.values.find(e => e.duration == duration).getOrElse(throw new IllegalStateException(s"$duration is not an expected value. $errorMsg"))
  }

  def apply(duration: FiniteDuration): GrantLength = {

    GrantLength.values.find(e => e.duration == duration).getOrElse(throw new IllegalStateException(s"$duration is not an expected value. $errorMsg"))
  }

  def show(grantLength: GrantLength): String = grantLength match {
    case FOUR_HOURS => "4 hours"
    case ONE_DAY => "1 day"
    case ONE_MONTH => "1 month"
    case THREE_MONTHS => "3 months"
    case SIX_MONTHS => "6 months"
    case ONE_YEAR => "1 year"
    case EIGHTEEN_MONTHS => "18 months"
    case THREE_YEARS => "3 years"
    case FIVE_YEARS => "5 years"
    case TEN_YEARS => "10 years"
    case ONE_HUNDRED_YEARS => "100 years"
  }

  import play.api.libs.json.Reads._

  implicit val writesGrantLength: Writes[GrantLength] = implicitly[Writes[Int]].contramap(x => x.duration.toDays.toInt)

  /* ANJUM version 1. ChangeGrantLengthSpec runs. The Int test passes but the Duration test fails */
  implicit val readsGrantLength: Reads[GrantLength] = {
    implicitly[Reads[Int]].flatMapResult {
      case i: Int if (allowedIntValues.contains(i)) => JsSuccess(GrantLength.unsafeApply(i))
      case e => JsError(s"Invalid grant length $e")
    } match {
      case i: Reads[GrantLength] => i
      case _ => {
        println("\n*******We didn't get an Int")
        ((JsPath \ "grantLength" \ "length").read[Long] and
          (JsPath \ "grantLength" \ "unit").read[String]
          )(GrantLength.apply(_,_))
      }
    }
  }

  /* ANJUM version 2. Doesn't compile.
  implicit val readsGrantLength: Reads[GrantLength] = {
          implicitly[Reads[Int]].flatMapResult {
            case i: Int if (allowedIntValues.contains(i)) => JsSuccess(GrantLength.unsafeApply(i))
            case e => { implicitly[Reads[FiniteDuration]].flatMapResult {
              case j: FiniteDuration if (allowedValues.contains(j)) => JsSuccess(GrantLength.apply(j))
              case e => JsError(s"Invalid grant length $e")
              }
            }
          }
      }
*/

  /* ANJUM version 3. Tried to take the internalHeader / authBearerToken approach from TPDA MongoFormatters. Doesn't compile
implicit val readsGrantLength : Reads[GrantLength] = (
(__ \ "grantLength").read[FiniteDuration].orElse((__ \ "grantLength").read[GrantLengthAsInt].map((_.asFiniteDuration))
)(GrantLength.apply _)
*/

    /* NEIL'S version
        val l: Reads[Option[Long]] = (JsPath \ "length").readNullable[Long]
        val u: Reads[Option[String]] = (JsPath \ "unit").readNullable[String]
        (l,u) map {
          case (Some(l), Some(u)) => (l and u)(GrantLength.apply(_, _))


            val lengthReads: Reads[String] = (JsPath \ "length").read[Long]

            val nameResult: JsResult[String] = json.validate[String](lengthReads)

            nameResult match {
              case JsSuccess(nme, _) => println(s"Name: $nme")
              case e: JsError        => println(s"Errors: ${JsError.toJson(e)}")
            }
        }
    */

    /*  implicitly[Reads[Int]].flatMapResult {
        case i: Int if (allowedIntValues.contains(i)) => JsSuccess(GrantLength.unsafeApply(i))
        case _ => throw new IllegalArgumentException("Invalid grant length")
//                case i => JsError(s"Invalid grant length $i")
      }*/

  // phase 1
  // requests to TPA = """{"grantLength":"547"}"""
  // responses from TPA = """{"grantLength":"547"}""" OR  """{"length":"547", "unit": "DAYS"}"""

  // phase 2
  // requests to TPA = """{"length":"547", "unit": "DAYS"}"""
  // responses from TPA = """{"length":"547", "unit": "DAYS"}"""

}
