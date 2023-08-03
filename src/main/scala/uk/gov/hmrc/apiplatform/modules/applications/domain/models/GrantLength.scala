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

import play.api.libs.json._

case class GrantLength private (days: Int) extends AnyVal {
  override def toString() = GrantLength.show(this)
}

object GrantLength {
  // new required to avoid apply returning Option
  val ONE_DAY           = new GrantLength(1)
  val ONE_MONTH         = new GrantLength(30)
  val THREE_MONTHS      = new GrantLength(90)
  val SIX_MONTHS        = new GrantLength(180)
  val ONE_YEAR          = new GrantLength(365)
  val EIGHTEEN_MONTHS   = new GrantLength(547)
  val THREE_YEARS       = new GrantLength(1095)
  val FIVE_YEARS        = new GrantLength(1825)
  val TEN_YEARS         = new GrantLength(3650)
  val ONE_HUNDRED_YEARS = new GrantLength(36500)

  implicit val ordering: Ordering[GrantLength] = Ordering.by(unapply)
  val values                                   = SortedSet(ONE_DAY, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, EIGHTEEN_MONTHS, THREE_YEARS, FIVE_YEARS, TEN_YEARS, ONE_HUNDRED_YEARS)

  private val allowedIntegerValues = values.map(_.days)

  def unsafeApply(grantLengthInDays: Int): GrantLength = {
    val errorMsg: String = "It should only be one of ('1 day', '1 month', '3 months', '6 months', '1 year', '18 months', " +
      "'3 years', '5 years', '10 years', '100 years') represented in days"

    apply(grantLengthInDays).getOrElse(throw new IllegalStateException(s"$grantLengthInDays is not an expected value. $errorMsg"))
  }

  def apply(grantLengthInDays: Int): Option[GrantLength] = {
    GrantLength.values.find(e => e.days == grantLengthInDays)
  }

  def show(grantLength: GrantLength): String = grantLength match {
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

  implicit val writesGrantLength = Json.valueWrites[GrantLength]

  implicit val readsGrantLength: Reads[GrantLength] = implicitly[Reads[Int]].flatMapResult {
    case i if (allowedIntegerValues.contains(i)) => JsSuccess(GrantLength.unsafeApply(i))
    case i                                       => JsError(s"Invalid grant length $i")
  }
}
