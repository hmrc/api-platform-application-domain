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

import scala.reflect.ClassTag

import cats.data.NonEmptyList

import uk.gov.hmrc.apiplatform.modules.applications.query.ErrorsOr
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*

object ParamsCombinationValidator {
  import cats.implicits.*

  def first[T <: Param[_]](using params: List[Param[_]], ct: ClassTag[T]): Option[T] = params.collect {
    case qp: T => qp
  }.headOption

  def checkLastUsedParamsCombinations(params: List[NonUniqueFilterParam[_]]): ErrorsOr[Unit] =
    params.collect {
      case qp: LastUsedAfterQP  => qp
      case qp: LastUsedBeforeQP => qp
    } match {
      case LastUsedAfterQP(after) :: LastUsedBeforeQP(before) :: _ if after.isAfter(before) => "Cannot query for used after date that is after a given before date".invalidNel
      case _                                                                                => ().validNel
    }

  def checkSubscriptionsParamsCombinations(params: List[NonUniqueFilterParam[_]]): ErrorsOr[Unit] = {
    import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*

    params.collect {
      case qp: SubscriptionFilterParam[_] => qp
    }
      .sortBy(_.order) match {
      case NoSubscriptionsQP :: Nil                     => ().validNel
      case HasSubscriptionsQP :: Nil                    => ().validNel
      case ApiContextQP(_) :: ApiVersionNbrQP(_) :: Nil => ().validNel
      case ApiContextQP(_) :: Nil                       => ().validNel

      case NoSubscriptionsQP :: HasSubscriptionsQP :: _ => "Cannot query for no subscriptions and then query for subscriptions".invalidNel

      case NoSubscriptionsQP :: ApiContextQP(_) :: _    => "Cannot query for no subscriptions and then query context".invalidNel
      case NoSubscriptionsQP :: ApiVersionNbrQP(_) :: _ => "Cannot query for no subscriptions and then query version nbr".invalidNel

      case HasSubscriptionsQP :: ApiContextQP(_) :: _    => "Cannot query for any subscriptions and then query context".invalidNel
      case HasSubscriptionsQP :: ApiVersionNbrQP(_) :: _ => "Cannot query for any subscriptions and then query version nbr".invalidNel

      case ApiVersionNbrQP(_) :: _ => "Cannot query for a version without a context".invalidNel

      case _ => ().validNel
    }
  }

  def checkUniqueParamsCombinations(uniqueFilterParams: NonEmptyList[UniqueFilterParam[_]], otherFilterParams: List[NonUniqueFilterParam[_]]): ErrorsOr[Unit] = {
    // Cannot have more than one unique filter param
    // Cannot have a unqiue filter param and other filter params other than UserAgentQP or WantSubscriptions/WantSubscroiptionField/WantStateHistory

    val onlyHasAllowableOtherParams = otherFilterParams.find(_ match {
      case GenericUserAgentQP(_) => false
      case ApiGatewayUserAgentQP => false
      case EnvironmentQP(_)      => false
      case _                     => true
    }).fold[ErrorsOr[Unit]](().validNel)(_ => "Cannot mix unqiue and non-unique filter params".invalidNel)

    (uniqueFilterParams.head, uniqueFilterParams.tail.isEmpty) match {
      case (_, true) => onlyHasAllowableOtherParams
      case _         => "Cannot mix one or more unique query params (serverToken, clientId and applicationId)".invalidNel
    }
  }

  def checkVerificationCodeUsesDeleteExclusion(otherFilterParams: List[NonUniqueFilterParam[_]]): ErrorsOr[Unit] = {
    given List[NonUniqueFilterParam[_]] = otherFilterParams
    (first[VerificationCodeQP], first[ExcludeDeletedQP.type]) match {
      case (None, _)                                             => ().validNel
      case (Some(VerificationCodeQP(_)), Some(ExcludeDeletedQP)) => ().validNel
      case (Some(VerificationCodeQP(_)), _)                      => "Verification code queries must exclude deleted state".invalidNel
    }
  }

  def checkStreamed(streamed: Boolean, resultInSingleApp: Boolean): ErrorsOr[Unit] =
    if ((streamed && resultInSingleApp)) {
      "Cannot return streamed results with single application queries".invalidNel
    } else {
      ().validNel
    }

  def checkWants(wantSubcriptions: Boolean, wantSubscriptionFields: Boolean, wantStateHistory: Boolean, resultInPagination: Boolean, resultInSingleApp: Boolean): ErrorsOr[Unit] =
    if ((wantSubscriptionFields && !resultInSingleApp)) {
      "Cannot return subscription fields with any query other than single application queries".invalidNel
    } else if ((wantSubcriptions || wantStateHistory) && resultInPagination) {
      "Cannot return subscriptions or state history with paginated queries".invalidNel
    } else {
      ().validNel
    }

  def checkAppStateFilters(otherFilterParams: List[NonUniqueFilterParam[Any]]): ErrorsOr[Unit] = {
    val stateFilter = first[MatchOneStateQP](using otherFilterParams)
    val dateFilter  = first[AppStateBeforeDateQP](using otherFilterParams)

    (stateFilter, dateFilter) match {
      case (_, None) => ().validNel

      case (Some(MatchOneStateQP(_)), _) => ().validNel
      case (None, Some(_))               => "Cannot query state used before date without a state filter".invalidNel
      case _                             => "Cannot query state used before date without a single state filter".invalidNel
    }
  }

  def checkUserCombinations(otherFilterParams: List[NonUniqueFilterParam[Any]]): ErrorsOr[Unit] = {
    given List[NonUniqueFilterParam[_]] = otherFilterParams

    (first[UserIdQP], first[AdminUserIdQP], first[UserIdsQP]) match {
      case (None, None, None)    => ().validNel
      case (Some(_), None, None) => ().validNel
      case (None, Some(_), None) => ().validNel
      case (None, None, Some(_)) => ().validNel
      case _                     => "Cannot query with multiple user id based filters".invalidNel
    }
  }

  def checkLimit(resultInPagination: Boolean, resultInSingleApp: Boolean, limitRequested: Boolean): ErrorsOr[Unit] =
    if (limitRequested && (resultInPagination || resultInSingleApp))
      "Cannot request limit on single query or paginated query".invalidNel
    else
      ().validNel

  def validateParamCombinations(using allParams: List[Param[_]]): ErrorsOr[Unit] = {
    val wantSubcriptions      = first[WantSubscriptionsQP.type].headOption.isDefined
    val wantSubcriptionFields = first[WantSubscriptionFieldsQP.type].headOption.isDefined
    val wantStateHistory      = first[WantStateHistoryQP.type].headOption.isDefined
    val streamed              = first[StreamedQP.type].headOption.isDefined
    val limitRequested        = first[LimitQP].headOption.isDefined

    val otherFilterParams  = allParams.collect {
      case fp: NonUniqueFilterParam[_] => fp
    }
    val uniqueFilterParams = allParams.collect(_ match {
      case ufp: UniqueFilterParam[_] => ufp
    })
    val sortingParams      = allParams.collect(_ match {
      case sp: SortingParam[_] => sp
    })
    val paginationParams   = allParams.collect(_ match {
      case pp: PaginationParam[_] => pp
    })
    val resultInPagination = paginationParams.headOption.isDefined
    val resultInSingleApp  = uniqueFilterParams.headOption.isDefined

    ((uniqueFilterParams, otherFilterParams, sortingParams, paginationParams) match {
      case (Nil, Nil, Nil, Nil)  => ().validNel // Only GK
      case (Nil, Nil, _, _)      => ().validNel
      case (Nil, _, _, _)        => ().validNel
      case (_, Nil, Nil, Nil)    => ().validNel
      case (h :: t, f, Nil, Nil) => checkUniqueParamsCombinations(NonEmptyList(h, t), f)
      case (h :: t, f, _, _)     => checkUniqueParamsCombinations(NonEmptyList(h, t), f) combine "Cannot mix unique queries with sorting or pagination".invalidNel
    })
      .combine(checkSubscriptionsParamsCombinations(otherFilterParams))
      .combine(checkLastUsedParamsCombinations(otherFilterParams))
      .combine(checkVerificationCodeUsesDeleteExclusion(otherFilterParams))
      .combine(checkUserCombinations(otherFilterParams))
      .combine(checkStreamed(streamed, resultInSingleApp))
      .combine(checkWants(wantSubcriptions, wantSubcriptionFields, wantStateHistory, resultInPagination, resultInSingleApp))
      .combine(checkLimit(resultInPagination, resultInSingleApp, limitRequested))
      .combine(checkAppStateFilters(otherFilterParams))
  }
}
