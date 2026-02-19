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

package uk.gov.hmrc.apiplatform.modules.applications.query.domain.services

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util as ju
import scala.util.Try
import scala.util.control.Exception.allCatch

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.syntax.all.*

import uk.gov.hmrc.apiplatform.modules.common.domain.models.*

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.AccessType
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*
import uk.gov.hmrc.apiplatform.modules.applications.query.{ErrorMessage, ErrorsOr}

sealed trait QueryParamsValidator {
  def paramName: ParamName
  def validate(values: Seq[String]): ErrorsOr[Param[_]]
}

object QueryParamsValidator {

  object NoValueExpected {

    def apply(paramName: ParamName)(values: Seq[String]): ErrorsOr[Unit] =
      values.toList match {
        case Nil => ().validNel
        case _   => s"No query value is allowed for $paramName".invalidNel
      }
  }

  object SingleValueExpected {

    def apply(paramName: ParamName)(values: Seq[String]): ErrorsOr[String] =
      values.toList match {
        case Nil           => s"$paramName requires a single value".invalidNel
        case single :: Nil => single.validNel
        case _             => s"Multiple $paramName query parameters are not permitted".invalidNel
      }
  }

  object AtLeastOneValue {

    def apply(paramName: ParamName)(values: Seq[String]): ErrorsOr[Seq[String]] =
      values.toList match {
        case Nil => s"$paramName requires at least one value".invalidNel
        case _   => values.validNel
      }
  }

  object OptionalValueAllowed {

    def apply(paramName: ParamName)(values: Seq[String]): ErrorsOr[Option[String]] =
      values.toList match {
        case Nil           => None.validNel
        case single :: Nil => single.some.validNel
        case _             => s"Multiple $paramName query parameters are not permitted".invalidNel
      }
  }

  object BooleanValueExpected {
    def apply(paramName: ParamName)(value: String): ErrorsOr[Boolean] = value.toBooleanOption.toValidNel(s"$paramName must be true or false")
  }

  object IntValueExpected {
    def apply(paramName: ParamName)(value: String): ErrorsOr[Int] = value.toIntOption.toValidNel(s"$paramName must be an integer value")
  }

  object PositiveIntValueExpected {

    def apply(paramName: ParamName)(value: String): ErrorsOr[Int] = IntValueExpected(paramName)(value) andThen { i =>
      i.some.filter(_ > 0).toValidNel(s"$paramName must be an positive integer value")
    }
  }

  object InstantValueExpected {

    def apply(paramName: ParamName)(value: String): ErrorsOr[Instant] = {
      Try(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value)))
        .toOption
        .fold[ErrorsOr[Instant]](s"$paramName of $value must be a valid date".invalidNel)(d => d.validNel)
    }
  }

  object ApplicationIdExpected {
    def apply(paramName: ParamName)(value: String): ErrorsOr[ApplicationId] = ApplicationId(value).toValidNel(s"$value is not a valid $paramName")
  }

  object ApplicationIdValidator extends QueryParamsValidator {
    val paramName = ParamName.ApplicationId

    def validate(values: Seq[String]): ErrorsOr[ApplicationIdQP] = {
      SingleValueExpected(paramName)(values) andThen ApplicationIdExpected(paramName) map { ApplicationIdQP(_) }
    }
  }

  object ClientIdValidator extends QueryParamsValidator {
    val paramName = ParamName.ClientId

    def validate(values: Seq[String]): ErrorsOr[ClientIdQP] = {
      SingleValueExpected(paramName)(values) map { s => ClientIdQP(ClientId(s)) }
    }
  }

  object ApiContextValidator extends QueryParamsValidator {
    val paramName = ParamName.ApiContext

    def validate(values: Seq[String]): ErrorsOr[ApiContextQP] = {
      SingleValueExpected(paramName)(values) map { s => ApiContextQP(ApiContext(s)) }
    }
  }

  object ApiVersionNbrValidator extends QueryParamsValidator {
    val paramName = ParamName.ApiVersionNbr

    def validate(values: Seq[String]): ErrorsOr[ApiVersionNbrQP] = {
      SingleValueExpected(paramName)(values) map { s => ApiVersionNbrQP(ApiVersionNbr(s)) }
    }
  }

  object HasSubscriptionsValidator extends QueryParamsValidator {
    val paramName = ParamName.HasSubscriptions

    def validate(values: Seq[String]): ErrorsOr[HasSubscriptionsQP.type] = {
      NoValueExpected(paramName)(values) map { _ => HasSubscriptionsQP }
    }
  }

  object NoSubscriptionsValidator extends QueryParamsValidator {
    val paramName = ParamName.NoSubscriptions

    def validate(values: Seq[String]): ErrorsOr[NoSubscriptionsQP.type] = {
      NoValueExpected(paramName)(values) map { _ => NoSubscriptionsQP }
    }
  }

  object WantSubscriptionsValidator extends QueryParamsValidator {
    val paramName = ParamName.WantSubscriptions

    def validate(values: Seq[String]): ErrorsOr[WantSubscriptionsQP.type] = {
      NoValueExpected(paramName)(values) map { _ => WantSubscriptionsQP }
    }
  }

  object WantSubscriptionFieldsValidator extends QueryParamsValidator {
    val paramName = ParamName.WantSubscriptionFields

    def validate(values: Seq[String]): ErrorsOr[WantSubscriptionFieldsQP.type] = {
      NoValueExpected(paramName)(values) map { _ => WantSubscriptionFieldsQP }
    }
  }

  object WantStateHistoryValidator extends QueryParamsValidator {
    val paramName = ParamName.WantStateHistory

    def validate(values: Seq[String]): ErrorsOr[WantStateHistoryQP.type] = {
      NoValueExpected(paramName)(values) map { _ => WantStateHistoryQP }
    }
  }

  object PageSizeValidator extends QueryParamsValidator {
    val paramName = ParamName.PageSize

    def validate(values: Seq[String]): ErrorsOr[PageSizeQP] = {
      SingleValueExpected(paramName)(values) andThen PositiveIntValueExpected(paramName) map { PageSizeQP(_) }
    }
  }

  object PageNbrValidator extends QueryParamsValidator {
    val paramName = ParamName.PageNbr

    def validate(values: Seq[String]): ErrorsOr[PageNbrQP] = {
      SingleValueExpected(paramName)(values) andThen PositiveIntValueExpected(paramName) map { PageNbrQP(_) }
    }
  }

  private object AppStateFilterExpected {
    import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.State

    def applyState(value: String): Option[State] = value match {
      case "CREATED"                        => State.Testing.some
      case "PENDING_GATEKEEPER_CHECK"       => State.PendingGatekeeperApproval.some
      case "PENDING_SUBMITTER_VERIFICATION" => State.PendingRequesterVerification.some
      case text                             => State(text)
    }

    def applyOne(value: String): Option[AppStateParam[_]] =
      value match {
        case "ACTIVE"            => ActiveStateQP.some
        case "EXCLUDING_DELETED" => ExcludeDeletedQP.some
        case "BLOCKED"           => BlockedStateQP.some
        case "ANY"               => NoStateFilteringQP.some
        //
        case text                => applyState(text).map(MatchOneStateQP(_))
      }

    def applyMany(values: Seq[String]): Option[AppStateParam[_]] = values match {
      case v :: Nil => applyOne(v)
      case vs       =>
        vs.toList
          .map(applyState(_))
          .traverse(identity)
          .map(ss => MatchManyStatesQP(NonEmptyList.fromListUnsafe(ss)))
    }

    def apply(values: Seq[String]): ErrorsOr[AppStateParam[_]] = applyMany(values).toValidNel(s"$values contains invalid parameters")
  }

  object StatusValidator extends QueryParamsValidator {
    val paramName = ParamName.Status

    def validate(values: Seq[String]): ErrorsOr[AppStateParam[_]] = {
      AtLeastOneValue(paramName)(values.flatMap(v => v.split(","))) andThen AppStateFilterExpected.apply
    }
  }

  object StatusBeforeDate extends QueryParamsValidator {
    val paramName = ParamName.StatusDateBefore

    def validate(values: Seq[String]): ErrorsOr[Param[_]] = {
      SingleValueExpected(paramName)(values) andThen InstantValueExpected.apply(paramName) map (date => AppStateBeforeDateQP(date))
    }
  }

  private object SortExpected {
    def apply(value: String): ErrorsOr[Sorting] = Sorting(value).toValidNel(s"$value is not a valid sort")
  }

  object SortValidator extends QueryParamsValidator {
    val paramName = ParamName.Sort

    def validate(values: Seq[String]): ErrorsOr[SortQP] = {
      SingleValueExpected(paramName)(values) andThen SortExpected.apply map { sort => SortQP(sort) }
    }
  }

  private object UserIdExpected {
    def apply(paramName: ParamName)(value: String): ErrorsOr[UserId] = UserId.apply(value).toValidNel(s"$value is not a valid $paramName")
  }

  object UserIdValidator extends QueryParamsValidator {
    val paramName = ParamName.UserId

    def validate(values: Seq[String]): ErrorsOr[UserIdQP] = {
      SingleValueExpected(paramName)(values) andThen UserIdExpected(paramName) map { UserIdQP(_) }
    }
  }

  object AdminUserIdValidator extends QueryParamsValidator {
    val paramName = ParamName.AdminUserId

    def validate(values: Seq[String]): ErrorsOr[AdminUserIdQP] = {
      SingleValueExpected(paramName)(values) andThen UserIdExpected(paramName) map { AdminUserIdQP(_) }
    }
  }

  object UserIdsValidator extends QueryParamsValidator {
    val paramName = ParamName.UserIds

    def validate(values: Seq[String]): ErrorsOr[UserIdsQP] = {
      def validateEach(vs: List[String]): List[ErrorsOr[List[UserId]]] = {
        vs.map(v => UserIdExpected(paramName)(v).map(List(_)))
      }

      AtLeastOneValue(paramName)(values.flatMap(v => v.split(","))).andThen { vs =>
        validateEach(vs.toList).combineAll
      }
        .map { UserIdsQP(_) }
    }
  }

  private object OrganisationIdExpected {

    def apply(paramName: ParamName)(value: String): ErrorsOr[OrganisationId] =
      allCatch.opt(OrganisationId(ju.UUID.fromString(value))).toValidNel(s"$value is not a valid $paramName")
  }

  object OrganisationIdValidator extends QueryParamsValidator {
    val paramName = ParamName.OrganisationId

    def validate(values: Seq[String]): ErrorsOr[OrganisationIdQP] = {
      SingleValueExpected(paramName)(values) andThen OrganisationIdExpected(paramName) map { OrganisationIdQP(_) }
    }
  }

  object AccessTypeValidator extends QueryParamsValidator {

    def parseText(value: String): ErrorsOr[Option[AccessType]] = {
      value match {
        case "ANY" => None.validNel
        case text  => AccessType(text).fold[ErrorsOr[Option[AccessType]]](
            s"$value is not a valid access type".invalidNel
          )(at =>
            at.some.validNel
          )
      }
    }
    val paramName                                              = ParamName.AccessType

    def validate(values: Seq[String]): ErrorsOr[AccessTypeParam[_]] = {
      SingleValueExpected(paramName)(values) andThen parseText map { ot => ot.fold[AccessTypeParam[_]](AnyAccessTypeQP)(accessType => MatchAccessTypeQP(accessType)) }
    }
  }

  object SearchTextValidator extends QueryParamsValidator {
    val paramName = ParamName.Search

    def validate(values: Seq[String]): ErrorsOr[SearchTextQP] = {
      SingleValueExpected(paramName)(values) map { text => SearchTextQP(text) }
    }
  }

  object NameValidator extends QueryParamsValidator {
    val paramName = ParamName.Name

    def validate(values: Seq[String]): ErrorsOr[NameQP] = {
      SingleValueExpected(paramName)(values) map { NameQP.apply }
    }
  }

  object VerificationCodeValidator extends QueryParamsValidator {
    val paramName = ParamName.VerificationCode

    def validate(values: Seq[String]): ErrorsOr[VerificationCodeQP] = {
      SingleValueExpected(paramName)(values) map { VerificationCodeQP.apply }
    }
  }

  object IncludeDeletedValidator extends QueryParamsValidator {
    val paramName = ParamName.IncludeDeleted

    def validate(values: Seq[String]): ErrorsOr[IncludeDeletedQP.type] = {
      OptionalValueAllowed(paramName)(values) andThen {
        case None       => IncludeDeletedQP.validNel
        case Some(text) =>
          text.toBooleanOption
            .fold[ErrorsOr[IncludeDeletedQP.type]](
              s"$paramName must be true or blank".invalidNel
            )(bool =>
              if (bool)
                IncludeDeletedQP.validNel
              else
                s"$paramName cannot be specified as false".invalidNel
            )
      }
    }
  }

  object DeleteRestrictionValidator extends QueryParamsValidator {
    val paramName = ParamName.DeleteRestriction

    def validate(values: Seq[String]): ErrorsOr[DeleteRestrictionQP] =
      SingleValueExpected(paramName)(values) andThen {
        _ match {
          case "DO_NOT_DELETE"  => DoNotDeleteQP.validNel
          case "NO_RESTRICTION" => NoRestrictionQP.validNel
          case value            => s"$value is not a valid delete restriction filter".invalidNel
        }
      }
  }

  private object EnvironmentExpected {
    def apply(value: String): ErrorsOr[Environment] = Environment.apply(value).toValidNel(s"$value is not a valid environment")

  }

  object EnvironmentValidator extends QueryParamsValidator {
    val paramName = ParamName.Environment

    def validate(values: Seq[String]): ErrorsOr[EnvironmentQP] = {
      SingleValueExpected(paramName)(values) andThen EnvironmentExpected.apply map { value => EnvironmentQP(value) }
    }
  }

  object LastUseBeforeValidator extends QueryParamsValidator {
    val paramName = ParamName.LastUsedBefore

    def validate(values: Seq[String]): ErrorsOr[LastUsedBeforeQP] = {
      SingleValueExpected(paramName)(values) andThen InstantValueExpected(paramName) map { date => LastUsedBeforeQP(date) }
    }
  }

  object LastUseAfterValidator extends QueryParamsValidator {
    val paramName = ParamName.LastUsedAfter

    def validate(values: Seq[String]): ErrorsOr[LastUsedAfterQP] = {
      SingleValueExpected(paramName)(values) andThen InstantValueExpected(paramName) map { date => LastUsedAfterQP(date) }
    }
  }

  object LimitValidator extends QueryParamsValidator {
    val paramName = ParamName.Limit

    def validate(values: Seq[String]): ErrorsOr[LimitQP] = {
      SingleValueExpected(paramName)(values) andThen PositiveIntValueExpected(paramName) map { v => LimitQP(v) }
    }
  }

  private val paramValidators: List[QueryParamsValidator] = List(
    QueryParamsValidator.AccessTypeValidator,
    QueryParamsValidator.ApiContextValidator,
    QueryParamsValidator.ApiVersionNbrValidator,
    QueryParamsValidator.ApplicationIdValidator,
    QueryParamsValidator.ClientIdValidator,
    QueryParamsValidator.DeleteRestrictionValidator,
    QueryParamsValidator.EnvironmentValidator,
    QueryParamsValidator.HasSubscriptionsValidator,
    QueryParamsValidator.IncludeDeletedValidator,
    QueryParamsValidator.LastUseBeforeValidator,
    QueryParamsValidator.LastUseAfterValidator,
    QueryParamsValidator.NameValidator,
    QueryParamsValidator.NoSubscriptionsValidator,
    QueryParamsValidator.PageSizeValidator,
    QueryParamsValidator.PageNbrValidator,
    QueryParamsValidator.StatusBeforeDate,
    QueryParamsValidator.StatusValidator,
    QueryParamsValidator.SortValidator,
    QueryParamsValidator.SearchTextValidator,
    QueryParamsValidator.UserIdValidator,
    QueryParamsValidator.AdminUserIdValidator,
    QueryParamsValidator.UserIdsValidator,
    QueryParamsValidator.VerificationCodeValidator,
    QueryParamsValidator.OrganisationIdValidator,
    QueryParamsValidator.WantSubscriptionsValidator,
    QueryParamsValidator.WantSubscriptionFieldsValidator,
    QueryParamsValidator.WantStateHistoryValidator,
    QueryParamsValidator.LimitValidator
  )

  private val validatorLookup: Map[String, QueryParamsValidator] = paramValidators.map(pv => pv.paramName.text.toLowerCase -> pv).toMap

  def parseParams(rawQueryParams: Map[String, Seq[String]]): ErrorsOr[List[Param[_]]] = {
    val paramValidations = rawQueryParams.map {
      case (k, vs) =>
        val validator = validatorLookup.get(k.toLowerCase).toValidNel(s"$k is not a valid query parameter")
        validator.andThen(_.validate(vs.filterNot(_.isBlank())))
    }

    val z: ValidatedNel[ErrorMessage, List[Param[_]]] = List.empty.validNel

    paramValidations.foldRight(z) {
      case (Valid(p1), Valid(p2))     => Valid(p1 :: p2)
      case (Invalid(e1), Invalid(e2)) => Invalid(e1 <+> e2)
      case (Invalid(e1), Valid(_))    => Invalid(e1)
      case (Valid(_), Invalid(e2))    => Invalid(e2)
    }
  }
}
