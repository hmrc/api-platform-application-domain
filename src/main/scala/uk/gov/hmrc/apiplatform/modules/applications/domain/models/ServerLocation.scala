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

sealed trait ServerLocation

object ServerLocation {
  case object InUK                      extends ServerLocation
  case object InEEA                     extends ServerLocation
  case object OutsideEEAWithAdequacy    extends ServerLocation
  case object OutsideEEAWithoutAdequacy extends ServerLocation

  val values = List(InUK, InEEA, OutsideEEAWithAdequacy, OutsideEEAWithoutAdequacy)

  def apply(text: String): Option[ServerLocation] = ServerLocation.values.find(_.toString.toUpperCase == text.toUpperCase())

  def unsafeApply(text: String): ServerLocation = apply(text).getOrElse(throw new RuntimeException(s"$text is not a valid Server Location"))

  import uk.gov.hmrc.apiplatform.modules.common.domain.services.SealedTraitJsonFormatting
  import play.api.libs.json.Format

  private val encodings: Map[ServerLocation, String] =
    Map(InUK -> "inUK", InEEA -> "inEEA", OutsideEEAWithAdequacy -> "outsideEEAWithAdequacy", OutsideEEAWithoutAdequacy -> "outsideEEAWithoutAdequacy")

  implicit val format: Format[ServerLocation] = SealedTraitJsonFormatting.createFormatFor[ServerLocation]("Server Location", apply, sl => encodings(sl))
}
