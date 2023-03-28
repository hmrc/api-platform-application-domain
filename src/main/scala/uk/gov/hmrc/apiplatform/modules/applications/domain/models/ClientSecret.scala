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

import java.{util => ju}
import java.time.LocalDateTime


// TODO - make id use clientSecret.Id
//
case class ClientSecret(id: String, name: String, createdOn: LocalDateTime, lastAccess: Option[LocalDateTime] = None)

object ClientSecret {
  import play.api.libs.json.Json

  case class Id(value: ju.UUID) extends AnyVal

  object Id {
    def random = Id(ju.UUID.randomUUID())

    implicit val format = Json.valueFormat[Id]
  }

  import uk.gov.hmrc.apiplatform.modules.common.domain.services.LocalDateTimeFormatter._
  implicit val format     = Json.format[ClientSecret]
}
