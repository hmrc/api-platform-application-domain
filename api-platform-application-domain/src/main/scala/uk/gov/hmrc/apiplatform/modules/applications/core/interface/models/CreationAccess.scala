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

package uk.gov.hmrc.apiplatform.modules.applications.core.interface.models

import play.api.libs.json._

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.AccessType

sealed trait CreationAccess {
  val accessType: AccessType = CreationAccess.accessType(this)
}

object CreationAccess {

  def accessType(access: CreationAccess): AccessType = access match {
    case _ @CreationAccess.Standard   => AccessType.STANDARD
    case _: CreationAccess.Privileged => AccessType.PRIVILEGED
  }

  case object Standard                                   extends CreationAccess
  case class Privileged(scopes: Set[String] = Set.empty) extends CreationAccess

  import uk.gov.hmrc.play.json.Union

  private implicit val formatPrivileged: OFormat[CreationAccess.Privileged] = Json.format[CreationAccess.Privileged]

  implicit val format: OFormat[CreationAccess] = Union.from[CreationAccess]("accessType")
    .andType[CreationAccess.Standard.type](AccessType.STANDARD.toString, () => CreationAccess.Standard)
    .and[CreationAccess.Privileged](AccessType.PRIVILEGED.toString)
    .format
}
