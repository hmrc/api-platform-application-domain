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

import uk.gov.hmrc.apiplatform.modules.common.domain.services.EnumJsonHelper.asScreamingSnakeCase

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.State
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.ApplicationQuery.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*

object QueryParamsToQueryStringMap {

  def toQuery(qry: ApplicationQuery): Map[ParamName, Seq[String]] = {
    qry match {
      case s: SingleApplicationQuery           => toQuery(s)
      case g: GeneralOpenEndedApplicationQuery => toQuery(g)
      case p: PaginatedApplicationQuery        => toQuery(p)
    }
  }

  private def toQuery(qry: SingleApplicationQuery): Map[ParamName, Seq[String]] = {
    qry match {
      case ById(id, _, wantSubscriptions, wantSubscriptionFields, wantStateHistory)                =>
        Map(ParamName.ApplicationId -> Seq(id.toString)) ++
          paramForWantSubs(wantSubscriptions) ++ paramForWantSubsFields(wantSubscriptionFields) ++ paramForWantStateHistory(wantStateHistory)
      case ByClientId(clientId, _, _, wantSubscriptions, wantSubscriptionFields, wantStateHistory) =>
        Map(ParamName.ClientId -> Seq(clientId.value)) ++
          paramForWantSubs(wantSubscriptions) ++ paramForWantSubsFields(wantSubscriptionFields) ++ paramForWantStateHistory(wantStateHistory)
      case ByServerToken(token, _, _, wantSubscriptions, wantSubscriptionFields, wantStateHistory) =>
        Map(ParamName.ServerToken -> Seq(token)) ++
          paramForWantSubs(wantSubscriptions) ++ paramForWantSubsFields(wantSubscriptionFields) ++ paramForWantStateHistory(wantStateHistory)
    }
  }

  private def toQuery(qry: GeneralOpenEndedApplicationQuery): Map[ParamName, Seq[String]] = {
    paramsFor(qry.params) ++ paramForSorting(qry.sorting) ++ paramForWantSubs(qry.wantSubscriptions) ++ paramForWantStateHistory(qry.wantStateHistory) ++ paramForLimit(qry.limit)
  }

  private def toQuery(qry: PaginatedApplicationQuery): Map[ParamName, Seq[String]] = {
    paramsFor(qry.params) ++ paramForSorting(qry.sorting) ++ paramsForPagination(qry.pagination)
  }

  private def paramValueForState(state: State): String = {
    state match {
      case State.Testing                      => "CREATED"
      case State.PendingGatekeeperApproval    => "PENDING_GATEKEEPER_CHECK"
      case State.PendingRequesterVerification => "PENDING_SUBMITTER_VERIFICATION"
      case s                                  => s.asScreamingSnakeCase
    }
  }

  def paramForWantSubs(wantSubscriptions: Boolean): Map[ParamName, Seq[String]] = {
    if (wantSubscriptions)
      Map(ParamName.WantSubscriptions -> Seq.empty)
    else
      Map.empty
  }

  def paramForWantSubsFields(wantSubscriptionFields: Boolean): Map[ParamName, Seq[String]] = {
    if (wantSubscriptionFields)
      Map(ParamName.WantSubscriptionFields -> Seq.empty)
    else
      Map.empty
  }

  def paramForWantStateHistory(wantStateHistory: Boolean): Map[ParamName, Seq[String]] = {
    if (wantStateHistory)
      Map(ParamName.WantStateHistory -> Seq.empty)
    else
      Map.empty
  }

  def paramForLimit(limit: Option[Int]): Map[ParamName, Seq[String]] = {
    limit.fold[Map[ParamName, Seq[String]]](Map.empty)(value => Map(ParamName.Limit -> Seq(value.toString())))
  }

  def paramForSorting(sort: Sorting): Map[ParamName, Seq[String]] = {
    if (sort != Sorting.NoSorting)
      Map(ParamName.Sort -> Seq(Sorting.asText(sort)))
    else
      Map.empty
  }

  def paramsForPagination(pagination: Pagination): Map[ParamName, Seq[String]] = {
    import Pagination.Defaults.*

    pagination match {
      // We always need to ensure one param is returned even if it's all defaulted, as this indicates a paginated query
      case Pagination(PageSize, PageNbr) => Map(ParamName.PageNbr -> Seq("1"))
      case Pagination(sz, PageNbr)       => Map(ParamName.PageSize -> Seq(sz.toString))
      case Pagination(PageSize, nbr)     => Map(ParamName.PageNbr -> Seq(nbr.toString))
      case Pagination(sz, nbr)           => Map(ParamName.PageSize -> Seq(sz.toString), ParamName.PageNbr -> Seq(nbr.toString))
    }
  }

  private def paramValueForInstant(instant: Instant): String = {
    DateTimeFormatter.ISO_INSTANT.format(instant)
  }

  private def paramsFor(params: List[NonUniqueFilterParam[_]]): Map[ParamName, Seq[String]] = {
    import cats.syntax.option.*

    params.map(_ match {
      case _: UserAgentParam[_]        => None
      //
      case NoSubscriptionsQP           => (ParamName.NoSubscriptions   -> Seq.empty).some
      case HasSubscriptionsQP          => (ParamName.HasSubscriptions  -> Seq.empty).some
      case ApiContextQP(value)         => (ParamName.ApiContext        -> Seq(value.toString)).some
      case ApiVersionNbrQP(value)      => (ParamName.ApiVersionNbr     -> Seq(value.toString)).some
      case LastUsedAfterQP(value)      => (ParamName.LastUsedAfter     -> Seq(paramValueForInstant(value))).some
      case LastUsedBeforeQP(value)     => (ParamName.LastUsedBefore    -> Seq(paramValueForInstant(value))).some
      case UserIdQP(value)             => (ParamName.UserId            -> Seq(value.toString)).some
      case AdminUserIdQP(value)        => (ParamName.AdminUserId       -> Seq(value.toString)).some
      case UserIdsQP(list)             => (ParamName.UserIds           -> Seq(list.mkString(","))).some
      case EnvironmentQP(value)        => (ParamName.Environment       -> Seq(value.toString.toUpperCase)).some
      case IncludeDeletedQP            => (ParamName.IncludeDeleted    -> Seq.empty).some
      case NoRestrictionQP             => (ParamName.DeleteRestriction -> Seq("NO_RESTRICTION")).some
      case DoNotDeleteQP               => (ParamName.DeleteRestriction -> Seq("DO_NOT_DELETE")).some
      case ActiveStateQP               => (ParamName.Status            -> Seq("ACTIVE")).some
      case ExcludeDeletedQP            => (ParamName.Status            -> Seq("EXCLUDING_DELETED")).some
      case BlockedStateQP              => (ParamName.Status            -> Seq("BLOCKED")).some
      case NoStateFilteringQP          => (ParamName.Status            -> Seq("ANY")).some
      case MatchOneStateQP(state)      => (ParamName.Status            -> Seq(paramValueForState(state))).some
      case MatchManyStatesQP(states)   => (ParamName.Status            -> states.toList.map(paramValueForState)).some
      case AppStateBeforeDateQP(value) => (ParamName.StatusDateBefore  -> Seq(paramValueForInstant(value))).some
      case SearchTextQP(value)         => (ParamName.Search            -> Seq(value)).some
      case NameQP(value)               => (ParamName.Name              -> Seq(value)).some
      case VerificationCodeQP(value)   => (ParamName.VerificationCode  -> Seq(value)).some
      case OrganisationIdQP(value)     => (ParamName.OrganisationId    -> Seq(value.toString)).some
      case MatchAccessTypeQP(value)    => (ParamName.AccessType        -> Seq(value.asScreamingSnakeCase)).some
      case AnyAccessTypeQP             => (ParamName.AccessType        -> Seq("ANY")).some
    }).collect {
      case Some(x) => x
    }.toMap
  }
}
