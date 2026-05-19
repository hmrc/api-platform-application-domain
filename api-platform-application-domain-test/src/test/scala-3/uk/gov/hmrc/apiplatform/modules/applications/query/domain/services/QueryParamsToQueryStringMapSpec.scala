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

import cats.data.NonEmptyList
import org.scalatest.EitherValues

import uk.gov.hmrc.apiplatform.modules.common.domain.models.*
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.*
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.State
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.ApplicationQuery.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*

class QueryParamsToQueryStringMapSpec extends HmrcSpec
    with EitherValues
    with ClientIdFixtures
    with UserIdFixtures
    with ApiIdentifierFixtures
    with ApplicationIdFixtures
    with OrganisationIdFixtures
    with FixedClock {

  def test(qry: ApplicationQuery, map: Map[ParamName, Seq[String]]): Unit = {
    QueryParamsToQueryStringMap.toQuery(qry) shouldBe map
  }

  def test(qry: ApplicationQuery, pairs: (ParamName, String)*): Unit = {
    test(qry, Map.empty[ParamName, Seq[String]] ++ pairs.map(p => p._1 -> Seq(p._2)))
  }

  def testGOEAQ(
      params: List[NonUniqueFilterParam[_]],
      sorting: Sorting = Sorting.NoSorting,
      wantSubscriptions: Boolean = false,
      limit: Option[Int] = None,
      streamed: Boolean = false
    )(
      pairs: (ParamName, String)*
    ): Unit = {
    test(GeneralOpenEndedApplicationQuery(params, sorting, wantSubscriptions, limit = limit, streamed = streamed), pairs: _*)
  }

  def testGOEAQMap(
      params: List[NonUniqueFilterParam[_]],
      sorting: Sorting = Sorting.NoSorting,
      wantSubscriptions: Boolean = false,
      limit: Option[Int] = None,
      streamed: Boolean = false
    )(
      map: Map[ParamName, Seq[String]]
    ): Unit = {
    test(GeneralOpenEndedApplicationQuery(params, sorting, wantSubscriptions, limit = limit, streamed = streamed), map)
  }

  def testOfNoValue(qry: ApplicationQuery, param: ParamName): Unit = {
    QueryParamsToQueryStringMap.toQuery(qry) shouldBe Map(param -> Seq.empty)
  }

  def testGOEAQOfNoValue(
      params: List[NonUniqueFilterParam[_]],
      sorting: Sorting = Sorting.NoSorting,
      wantSubscriptions: Boolean = false
    )(
      param: ParamName
    ): Unit = {
    QueryParamsToQueryStringMap.toQuery(GeneralOpenEndedApplicationQuery(params, sorting, wantSubscriptions)) shouldBe Map(param -> Seq.empty)
  }

  "allApplications" should {
    "convert to query" in {
      test(ApplicationQueries.allApplications(true), ParamName.Status -> "EXCLUDING_DELETED")
      test(ApplicationQueries.allApplications(false))
    }
  }

  "wantSubscriptions" should {
    "convert for single query" in {
      test(
        ApplicationQuery.ByClientId(clientIdOne, recordUsage = false, List(ExcludeDeletedQP), wantSubscriptions = true),
        Map(
          ParamName.ClientId          -> Seq(clientIdOne.value),
          ParamName.WantSubscriptions -> Seq.empty
        )
      )
    }
    "convert for general query" in {
      test(
        GeneralOpenEndedApplicationQuery(List(AdminUserIdQP(userIdOne)), wantSubscriptions = true),
        Map(
          ParamName.AdminUserId       -> Seq(userIdOne.toString()),
          ParamName.WantSubscriptions -> Seq.empty
        )
      )
    }
  }

  "wantSubscriptionFields" should {
    "convert for single query" in {
      test(
        ApplicationQuery.ByClientId(clientIdOne, recordUsage = false, List(ExcludeDeletedQP), wantSubscriptionFields = true),
        Map(
          ParamName.ClientId               -> Seq(clientIdOne.value),
          ParamName.WantSubscriptionFields -> Seq.empty
        )
      )
    }
  }

  "wantStateHistory" should {
    "convert for single query" in {
      test(
        ApplicationQuery.ByClientId(clientIdOne, recordUsage = false, List(ExcludeDeletedQP), wantStateHistory = true),
        Map(
          ParamName.ClientId         -> Seq(clientIdOne.value),
          ParamName.WantStateHistory -> Seq.empty
        )
      )
    }
    "convert for general query" in {
      test(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), wantStateHistory = true),
        Map(
          ParamName.UserId           -> Seq(userIdOne.toString()),
          ParamName.WantStateHistory -> Seq.empty
        )
      )
    }
  }

  "streamed" should {
    "convert for general query" in {
      test(
        GeneralOpenEndedApplicationQuery(List(AdminUserIdQP(userIdOne)), streamed = true),
        Map(
          ParamName.AdminUserId -> Seq(userIdOne.toString()),
          ParamName.Streamed    -> Seq.empty
        )
      )
    }
    "convert for paginated query" in {
      test(
        PaginatedApplicationQuery(List(AdminUserIdQP(userIdOne)), streamed = true),
        Map(
          ParamName.AdminUserId -> Seq(userIdOne.toString()),
          ParamName.PageNbr     -> Seq("1"),
          ParamName.Streamed    -> Seq.empty
        )
      )
    }
  }

  "applicationByClientId" should {
    "convert to query" in {
      test(ApplicationQueries.applicationByClientId(clientIdOne), ParamName.ClientId -> (clientIdOne.value))
    }
  }

  "applicationsByName" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByName("bob"), ParamName.Name -> "bob", ParamName.Environment -> "PRODUCTION", ParamName.Status -> "EXCLUDING_DELETED")
    }
  }

  "applicationsByVerifiableUplift" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByVerifiableUplift("bob"), ParamName.VerificationCode -> "bob", ParamName.Status -> "EXCLUDING_DELETED")
    }
  }

  "applicationsByUserId" should {
    "convert to user query" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne, includeDeleted = true),
        Map(
          ParamName.UserId -> Seq(s"$userIdOne")
        )
      )
    }
    "convert to user query excluding deleted wanting subs" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne, wantSubscriptions = true),
        Map(
          ParamName.UserId            -> Seq(s"$userIdOne"),
          ParamName.Status            -> Seq("EXCLUDING_DELETED"),
          ParamName.WantSubscriptions -> Seq.empty
        )
      )
    }
    "convert to user query explicitly excluding deleted and denying subs" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne, includeDeleted = false, wantSubscriptions = false),
        Map(
          ParamName.UserId -> Seq(s"$userIdOne"),
          ParamName.Status -> Seq("EXCLUDING_DELETED")
        )
      )
    }
    "convert to user query excluding deleted" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne),
        Map(
          ParamName.UserId -> Seq(s"$userIdOne"),
          ParamName.Status -> Seq("EXCLUDING_DELETED")
        )
      )
    }
  }

  "applicationsByUserIdAndEnvironment" should {
    "convert to query" in {
      test(
        ApplicationQueries.applicationsByUserIdAndEnvironment(userIdOne, Environment.Sandbox, false),
        ParamName.UserId      -> s"$userIdOne",
        ParamName.Environment -> "SANDBOX",
        ParamName.Status      -> "EXCLUDING_DELETED"
      )
    }
    "convert to query wanting subscriptions" in {
      test(
        ApplicationQueries.applicationsByUserIdAndEnvironment(userIdOne, Environment.Production, true),
        Map(
          ParamName.UserId            -> Seq(s"$userIdOne"),
          ParamName.Environment       -> Seq("PRODUCTION"),
          ParamName.Status            -> Seq("EXCLUDING_DELETED"),
          ParamName.WantSubscriptions -> Seq.empty
        )
      )
    }
  }

  "applicationsByStateAndDate" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByStateAndDate(State.Production, instant), ParamName.Status -> "PRODUCTION", ParamName.StatusDateBefore -> nowAsText)
    }
  }

  "applicationsByStates" should {
    "convert to query" in {
      test(
        GeneralOpenEndedApplicationQuery(
          List(
            MatchManyStatesQP(NonEmptyList.one(State.PreProduction) ++ List(State.Production, State.PendingGatekeeperApproval))
          )
        ),
        Map(
          ParamName.Status -> Seq("PRE_PRODUCTION", "PRODUCTION", "PENDING_GATEKEEPER_CHECK")
        )
      )
    }
  }

  "applicationsByApiContext" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByApiContext(apiContextOne), ParamName.ApiContext -> s"$apiContextOne")
    }
  }

  "applicationsByApiIdentifier" should {
    "convert to query" in {
      test(
        ApplicationQueries.applicationsByApiIdentifier(apiIdentifierOne),
        ParamName.ApiContext    -> s"${apiIdentifierOne.context}",
        ParamName.ApiVersionNbr -> s"${apiIdentifierOne.versionNbr}"
      )
    }
  }

  "QueryParamsToQueryBuilder" should {
    "convert ById to query" should {
      test(ApplicationQuery.ById(applicationIdOne, Nil, false, false, false), ParamName.ApplicationId -> s"$applicationIdOne")

      test(
        ApplicationQuery.ById(applicationIdOne, Nil, true, true, true),
        Map(
          ParamName.ApplicationId          -> Seq(s"$applicationIdOne"),
          ParamName.WantSubscriptions      -> Seq.empty,
          ParamName.WantSubscriptionFields -> Seq.empty,
          ParamName.WantStateHistory       -> Seq.empty
        )
      )
      test(
        ApplicationQuery.ById(applicationIdOne, Nil, true, false, false),
        Map(
          ParamName.ApplicationId     -> Seq(s"$applicationIdOne"),
          ParamName.WantSubscriptions -> Seq.empty
        )
      )
      test(
        ApplicationQuery.ById(applicationIdOne, Nil, false, true, false),
        Map(
          ParamName.ApplicationId          -> Seq(s"$applicationIdOne"),
          ParamName.WantSubscriptionFields -> Seq.empty
        )
      )
    }
    "convert ByClientId to query" should {
      test(ApplicationQuery.ByClientId(clientIdOne, false, Nil, false, false, false), ParamName.ClientId -> s"$clientIdOne")
      test(
        ApplicationQuery.ByClientId(clientIdOne, false, Nil, true, true, true),
        Map(
          ParamName.ClientId               -> Seq(s"$clientIdOne"),
          ParamName.WantSubscriptions      -> Seq.empty,
          ParamName.WantSubscriptionFields -> Seq.empty,
          ParamName.WantStateHistory       -> Seq.empty
        )
      )
    }
    "convert ServerToken to query" should {
      test(ApplicationQuery.ByServerToken("bob", false, Nil, false, false, false), ParamName.ServerToken -> "bob")
      test(
        ApplicationQuery.ByServerToken("bob", false, Nil, true, true, true),
        Map(
          ParamName.ServerToken            -> Seq("bob"),
          ParamName.WantSubscriptions      -> Seq.empty,
          ParamName.WantSubscriptionFields -> Seq.empty,
          ParamName.WantStateHistory       -> Seq.empty
        )
      )
    }

    "convert GenericUserAgentQP to query" in {
      testGOEAQ(List(GenericUserAgentQP("bob")))()
    }
    "convert NoSubscriptionsQP to query" in {
      testGOEAQOfNoValue(List(NoSubscriptionsQP))(ParamName.NoSubscriptions)
    }
    "convert HasSubscriptionsQP to query" in {
      testGOEAQOfNoValue(List(HasSubscriptionsQP))(ParamName.HasSubscriptions)
    }
    "convert ApiContextQP to query" in {
      testGOEAQ(List(ApiContextQP(apiContextOne)))(ParamName.ApiContext -> s"$apiContextOne")
    }
    "convert ApiVersionNbrQP to query" in {
      testGOEAQ(List(ApiVersionNbrQP(apiVersionNbrOne)))(ParamName.ApiVersionNbr -> s"$apiVersionNbrOne")
    }
    "convert LastUsedAfterQP to query" in {
      testGOEAQ(List(LastUsedAfterQP(instant)))(ParamName.LastUsedAfter -> nowAsText)
    }
    "convert LastUsedBeforeQP to query" in {
      testGOEAQ(List(LastUsedBeforeQP(instant)))(ParamName.LastUsedBefore -> nowAsText)
    }
    "convert NeverUsedQP to query" in {
      testGOEAQOfNoValue(List(NeverUsedQP))(ParamName.NeverUsed)
    }
    "convert UserIdQP to query" in {
      testGOEAQ(List(UserIdQP(userIdOne)))(ParamName.UserId -> s"$userIdOne")
    }
    "convert AdminUserIdQP to query" in {
      testGOEAQ(List(AdminUserIdQP(userIdOne)))(ParamName.AdminUserId -> s"$userIdOne")
    }
    "convert OrganisationIdQP to query" in {
      testGOEAQ(List(OrganisationIdQP(organisationIdOne)))(ParamName.OrganisationId -> s"$organisationIdOne")
    }
    "convert EnvironmentQP to query" in {
      testGOEAQ(List(EnvironmentQP(Environment.Sandbox)))(ParamName.Environment -> "SANDBOX")
    }
    "convert IncludeDeletedQP to query" in {
      testGOEAQOfNoValue(List(IncludeDeletedQP))(ParamName.IncludeDeleted)
    }
    "convert NoRestrictionQP to query" in {
      testGOEAQ(List(NoRestrictionQP))(ParamName.DeleteRestriction -> "NO_RESTRICTION")
    }
    "convert DoNotDeleteQP to query" in {
      testGOEAQ(List(DoNotDeleteQP))(ParamName.DeleteRestriction -> "DO_NOT_DELETE")
    }
    "convert ActiveStateQP to query" in {
      testGOEAQ(List(ActiveStateQP))(ParamName.Status -> "ACTIVE")
    }
    "convert ExcludeDeletedQP to query" in {
      testGOEAQ(List(ExcludeDeletedQP))(ParamName.Status -> "EXCLUDING_DELETED")
    }
    "convert BlockedStateQP to query" in {
      testGOEAQ(List(BlockedStateQP))(ParamName.Status -> "BLOCKED")
    }
    "convert NoStateFilteringQP to query" in {
      testGOEAQ(List(NoStateFilteringQP))(ParamName.Status -> "ANY")
    }
    "convert MatchAccessTypeQP(value) to query" in {
      testGOEAQ(List(MatchAccessTypeQP(AccessType.Standard)))(ParamName.AccessType -> "STANDARD")
    }
    "convert MatchOneStateQP(value) to query" in {
      testGOEAQ(List(MatchOneStateQP(State.Production)))(ParamName.Status -> "PRODUCTION")
    }
    "convert MatchOneStateQP(PENDING_RI) to query" in {
      testGOEAQ(List(MatchOneStateQP(State.PendingRequesterVerification)))(ParamName.Status -> "PENDING_SUBMITTER_VERIFICATION")
    }
    "convert MatchManyStatesQP(value) to query" in {
      testGOEAQMap(List(MatchManyStatesQP(NonEmptyList.of(State.Production, State.Testing))))(Map(ParamName.Status -> Seq("PRODUCTION", "CREATED")))
    }
    "convert AppStateBeforeDateQP(value) to query" in {
      testGOEAQ(List(AppStateBeforeDateQP(instant)))(ParamName.StatusDateBefore -> nowAsText)
    }
    "convert SearchTextQP(value) to query" in {
      testGOEAQ(List(SearchTextQP("bob")))(ParamName.Search -> "bob")
    }
    "convert NameQP(value) to query" in {
      testGOEAQ(List(NameQP("bob")))(ParamName.Name -> "bob")
    }
    "convert VerificationCodeQP to query" in {
      testGOEAQ(List(VerificationCodeQP("bob")))(ParamName.VerificationCode -> "bob")
    }
    "convert AnyAccessTypeQP to query" in {
      testGOEAQ(List(AnyAccessTypeQP))(ParamName.AccessType -> "ANY")
    }
    "convert UserIdsQP to query" in {
      val users = List(userIdOne, userIdTwo)
      testGOEAQ(List(UserIdsQP(users)))(ParamName.UserIds -> users.map(_.toString).mkString(","))
    }
    "convert UserIdQP with pagination to query" in {
      test(PaginatedApplicationQuery(List(UserIdQP(userIdOne)), Sorting.NoSorting, Pagination()), ParamName.UserId -> s"$userIdOne", ParamName.PageNbr -> "1")
    }
  }

  "paramForLimit" should {
    "convert LimitQP to query" in {
      testGOEAQ(Nil, limit = Some(50))(ParamName.Limit -> "50")
    }
    "convert no LimitQP to query" in {
      testGOEAQ(Nil, limit = None)()
    }
  }

  "paramForSorting" should {
    "convert to blank on no sort" in {
      QueryParamsToQueryStringMap.paramForSorting(Sorting.NoSorting) shouldBe Map.empty
    }

    "convert to label" in {
      QueryParamsToQueryStringMap.paramForSorting(Sorting.NameAscending) shouldBe Map(ParamName.Sort -> Seq("NAME_ASC"))
    }
  }

  "paramsForPagination" should {
    import Pagination.Defaults

    "convert to query" in {
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(Defaults.PageSize, Defaults.PageNbr)) shouldBe Map(ParamName.PageNbr -> Seq("1"))
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(Defaults.PageSize, 5)) shouldBe Map(ParamName.PageNbr -> Seq("5"))
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(20, Defaults.PageNbr)) shouldBe Map(ParamName.PageSize -> Seq("20"))
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(20, 5)) shouldBe Map(ParamName.PageSize -> Seq("20"), ParamName.PageNbr -> Seq("5"))
    }
  }
}
