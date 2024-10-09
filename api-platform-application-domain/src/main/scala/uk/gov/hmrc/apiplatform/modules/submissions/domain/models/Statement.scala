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

package uk.gov.hmrc.apiplatform.modules.submissions.domain.models

import cats.data.NonEmptyList

import uk.gov.hmrc.apiplatform.modules.common.domain.services.NonEmptyListFormatters

sealed trait StatementFragment
sealed trait NonBulletStatementFragment             extends StatementFragment
sealed trait SimpleStatementFragment                extends NonBulletStatementFragment
case class StatementText(text: String)              extends SimpleStatementFragment
case class StatementLink(text: String, url: String) extends SimpleStatementFragment

case class StatementBullets(bullets: NonEmptyList[NonBulletStatementFragment]) extends StatementFragment

object StatementBullets {
  def apply(bullet: NonBulletStatementFragment, bullets: NonBulletStatementFragment*) = new StatementBullets(NonEmptyList.of(bullet, bullets: _*))
}

case class CompoundFragment(fragments: NonEmptyList[SimpleStatementFragment]) extends NonBulletStatementFragment

object CompoundFragment {
  def apply(fragment: SimpleStatementFragment, fragments: SimpleStatementFragment*) = new CompoundFragment(NonEmptyList.of(fragment, fragments: _*))
}

case class Statement(fragments: NonEmptyList[StatementFragment])

object Statement extends NonEmptyListFormatters {
  def apply(fragment: StatementFragment, fragments: StatementFragment*) = new Statement(NonEmptyList.of(fragment, fragments: _*))

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import uk.gov.hmrc.play.json.Union

  implicit val jsonFormatStatementText: OFormat[StatementText] = Json.format[StatementText]
  implicit val jsonFormatStatementLink: OFormat[StatementLink] = Json.format[StatementLink]

  implicit lazy val readsStatementBullets: Reads[StatementBullets] = (
    (__ \ "bullets").read(nelReads[NonBulletStatementFragment])
  )
    .map(StatementBullets(_))

  implicit lazy val writesStatementBullets: OWrites[StatementBullets] = (
    (
      (__ \ "bullets").write(nelWrites[NonBulletStatementFragment])
    )
      .contramap(unlift(StatementBullets.unapply))
  )

  implicit lazy val jsonFormatStatementBullets: OFormat[StatementBullets] = OFormat(readsStatementBullets, writesStatementBullets)

  implicit lazy val readsCompoundFragment: Reads[CompoundFragment] = (
    (__ \ "fragments").read(nelReads[SimpleStatementFragment])
  )
    .map(CompoundFragment(_))

  implicit lazy val writesCompoundFragment: OWrites[CompoundFragment] = (
    (
      (__ \ "fragments").write(nelWrites[SimpleStatementFragment])
    )
      .contramap(unlift(CompoundFragment.unapply))
  )

  implicit lazy val jsonFormatCompoundFragment: OFormat[CompoundFragment] = OFormat(readsCompoundFragment, writesCompoundFragment)

  implicit lazy val jsonFormatSimpleStatementFragment: Format[SimpleStatementFragment] = Union.from[SimpleStatementFragment]("statementType")
    .and[StatementText]("text")
    .and[StatementLink]("link")
    .format

  implicit lazy val jsonFormatNonBulletStatementFragment: Format[NonBulletStatementFragment] = Union.from[NonBulletStatementFragment]("statementType")
    .and[StatementText]("text")
    .and[StatementLink]("link")
    .andLazy[CompoundFragment]("compound", jsonFormatCompoundFragment)
    .format

  implicit lazy val jsonFormatStatementFragment: Format[StatementFragment] = Union.from[StatementFragment]("statementType")
    .and[StatementText]("text")
    .and[StatementLink]("link")
    .andLazy[StatementBullets]("bullets", jsonFormatStatementBullets)
    .andLazy[CompoundFragment]("compound", jsonFormatCompoundFragment)
    .format

  implicit val jsonFormatStatement: OFormat[Statement] = Json.format[Statement]
}
