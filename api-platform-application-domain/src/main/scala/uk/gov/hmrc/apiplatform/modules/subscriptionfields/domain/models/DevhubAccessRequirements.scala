/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models

case class DevhubAccessRequirements(
    read: DevhubAccessRequirement,
    write: DevhubAccessRequirement
  ) {
  def satisfiesRead(dal: DevhubAccessLevel): Boolean  = dal.satisfiesRequirement(read) // ReadWrite will be at least as strict.
  def satisfiesWrite(dal: DevhubAccessLevel): Boolean = dal.satisfiesRequirement(write)
}

object DevhubAccessRequirements {
  import DevhubAccessRequirement._

  final val Default = DevhubAccessRequirements(
    uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default,
    uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default
  )

  // Do not allow lesser restrictions on write than on read
  // - it would make no sense to allow NoOne read but everyone write or developer write and admin read
  //
  def apply(
      read: DevhubAccessRequirement,
      write: DevhubAccessRequirement = uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default
    ): DevhubAccessRequirements = (read, write) match {

    case (NoOne, _)          => new DevhubAccessRequirements(NoOne, NoOne) {}
    case (AdminOnly, Anyone) => new DevhubAccessRequirements(AdminOnly, AdminOnly) {}
    case _                   => new DevhubAccessRequirements(read, write) {}
  }

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  def ignoreDefaultField[T](value: T, default: T, jsonFieldName: String)(implicit w: Writes[T]) =
    if (value == default) None else Some((jsonFieldName, Json.toJsFieldJsValueWrapper(value)))

  implicit val readsDevhubAccessRequirements: Reads[DevhubAccessRequirements] = (
    ((JsPath \ "read").read[DevhubAccessRequirement] or Reads.pure(uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default)) and
      ((JsPath \ "write").read[DevhubAccessRequirement] or Reads.pure(uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default))
  )(DevhubAccessRequirements.apply _)

  implicit val writesDevhubAccessRequirements: OWrites[DevhubAccessRequirements] = new OWrites[DevhubAccessRequirements] {

    def writes(requirements: DevhubAccessRequirements) = {
      Json.obj(
        (
          ignoreDefaultField(requirements.read, uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default, "read") ::
            ignoreDefaultField(requirements.write, uk.gov.hmrc.apiplatform.modules.subscriptionfields.domain.models.DevhubAccessRequirement.Default, "write") ::
            List.empty[Option[(String, Json.JsValueWrapper)]]
        ).filterNot(_.isEmpty).map(_.get): _*
      )
    }
  }

}
