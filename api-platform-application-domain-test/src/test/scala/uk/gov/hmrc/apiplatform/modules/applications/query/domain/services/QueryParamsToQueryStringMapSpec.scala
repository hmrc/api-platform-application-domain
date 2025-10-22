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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiIdentifierFixtures, ApplicationIdFixtures, ClientIdFixtures, Environment, UserIdFixtures}
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models._
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.State
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.ApplicationQuery._
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param._
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models._

class QueryParamsToQueryStringMapSpec extends HmrcSpec with EitherValues with ClientIdFixtures with UserIdFixtures with ApiIdentifierFixtures with ApplicationIdFixtures
    with FixedClock {

  def test(qry: ApplicationQuery, map: Map[String, Seq[String]]): Unit = {
    QueryParamsToQueryStringMap.toQuery(qry) shouldBe map
  }

  def test(qry: ApplicationQuery, pairs: (String, String)*): Unit = {
    test(qry, Map.empty[String, Seq[String]] ++ pairs.map(p => p._1 -> Seq(p._2)))
  }

  def testGOEAQ(params: List[NonUniqueFilterParam[_]], sorting: Sorting = Sorting.NoSorting, wantSubscriptions: Boolean = false)(pairs: (String, String)*): Unit = {
    test(GeneralOpenEndedApplicationQuery(params, sorting, wantSubscriptions), pairs: _*)
  }

  def testGOEAQMap(params: List[NonUniqueFilterParam[_]], sorting: Sorting = Sorting.NoSorting, wantSubscriptions: Boolean = false)(map: Map[String, Seq[String]]): Unit = {
    test(GeneralOpenEndedApplicationQuery(params, sorting, wantSubscriptions), map)
  }

  def testOfNoValue(qry: ApplicationQuery, param: String): Unit = {
    QueryParamsToQueryStringMap.toQuery(qry) shouldBe Map(param -> Seq.empty)
  }

  def testGOEAQOfNoValue(params: List[NonUniqueFilterParam[_]], sorting: Sorting = Sorting.NoSorting, wantSubscriptions: Boolean = false)(param: String): Unit = {
    QueryParamsToQueryStringMap.toQuery(GeneralOpenEndedApplicationQuery(params, sorting, wantSubscriptions)) shouldBe Map(param -> Seq.empty)
  }

  "allApplications" should {
    "convert to query" in {
      test(ApplicationQueries.allApplications(true), ParamNames.Status -> "EXCLUDING_DELETED")
      test(ApplicationQueries.allApplications(false))
    }
  }

  "wantSubscriptions" should {
    "convert for single query" in {
      test(
        ApplicationQuery.ByClientId(clientIdOne, recordUsage = false, List(ExcludeDeletedQP), wantSubscriptions = true),
        Map(
          ParamNames.ClientId          -> Seq(clientIdOne.value),
          ParamNames.WantSubscriptions -> Seq.empty
        )
      )
    }
    "convert for general query" in {
      test(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), wantSubscriptions = true),
        Map(
          ParamNames.UserId            -> Seq(userIdOne.toString()),
          ParamNames.WantSubscriptions -> Seq.empty
        )
      )
    }
  }

  "wantSubscriptionFields" should {
    "convert for single query" in {
      test(
        ApplicationQuery.ByClientId(clientIdOne, recordUsage = false, List(ExcludeDeletedQP), wantSubscriptionFields = true),
        Map(
          ParamNames.ClientId               -> Seq(clientIdOne.value),
          ParamNames.WantSubscriptionFields -> Seq.empty
        )
      )
    }
    "convert for general query" in {
      test(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), wantSubscriptionFields = true),
        Map(
          ParamNames.UserId                 -> Seq(userIdOne.toString()),
          ParamNames.WantSubscriptionFields -> Seq.empty
        )
      )
    }
  }
  "wantStateHistory" should {
    "convert for single query" in {
      test(
        ApplicationQuery.ByClientId(clientIdOne, recordUsage = false, List(ExcludeDeletedQP), wantStateHistory = true),
        Map(
          ParamNames.ClientId         -> Seq(clientIdOne.value),
          ParamNames.WantStateHistory -> Seq.empty
        )
      )
    }
    "convert for general query" in {
      test(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), wantStateHistory = true),
        Map(
          ParamNames.UserId           -> Seq(userIdOne.toString()),
          ParamNames.WantStateHistory -> Seq.empty
        )
      )
    }
  }

  "applicationByClientId" should {
    "convert to query" in {
      test(ApplicationQueries.applicationByClientId(clientIdOne), ParamNames.ClientId -> (clientIdOne.value))
    }
  }

  "applicationsByName" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByName("bob"), ParamNames.Name -> "bob", ParamNames.Environment -> "PRODUCTION", ParamNames.Status -> "EXCLUDING_DELETED")
    }
  }

  "applicationsByVerifiableUplift" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByVerifiableUplift("bob"), ParamNames.VerificationCode -> "bob", ParamNames.Status -> "EXCLUDING_DELETED")
    }
  }

  "applicationsByUserId" should {
    "convert to user query" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne, includeDeleted = true),
        Map(
          ParamNames.UserId -> Seq(s"$userIdOne")
        )
      )
    }
    "convert to user query excluding deleted wanting subs" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne, wantSubscriptions = true),
        Map(
          ParamNames.UserId            -> Seq(s"$userIdOne"),
          ParamNames.Status            -> Seq("EXCLUDING_DELETED"),
          ParamNames.WantSubscriptions -> Seq.empty
        )
      )
    }
    "convert to user query explicitly excluding deleted and denying subs" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne, includeDeleted = false, wantSubscriptions = false),
        Map(
          ParamNames.UserId -> Seq(s"$userIdOne"),
          ParamNames.Status -> Seq("EXCLUDING_DELETED")
        )
      )
    }
    "convert to user query excluding deleted" in {
      test(
        ApplicationQueries.applicationsByUserId(userIdOne),
        Map(
          ParamNames.UserId -> Seq(s"$userIdOne"),
          ParamNames.Status -> Seq("EXCLUDING_DELETED")
        )
      )
    }
  }

  "applicationsByUserIdAndEnvironment" should {
    "convert to query" in {
      test(
        ApplicationQueries.applicationsByUserIdAndEnvironment(userIdOne, Environment.SANDBOX, false),
        ParamNames.UserId      -> s"$userIdOne",
        ParamNames.Environment -> "SANDBOX",
        ParamNames.Status      -> "EXCLUDING_DELETED"
      )
    }
    "convert to query wanting subscriptions" in {
      test(
        ApplicationQueries.applicationsByUserIdAndEnvironment(userIdOne, Environment.PRODUCTION, true),
        Map(
          ParamNames.UserId            -> Seq(s"$userIdOne"),
          ParamNames.Environment       -> Seq("PRODUCTION"),
          ParamNames.Status            -> Seq("EXCLUDING_DELETED"),
          ParamNames.WantSubscriptions -> Seq.empty
        )
      )
    }
  }

  "applicationsByStateAndDate" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByStateAndDate(State.PRODUCTION, instant), ParamNames.Status -> "PRODUCTION", ParamNames.StatusDateBefore -> nowAsText)
    }
  }

  "applicationsByStates" should {
    "convert to query" in {
      test(
        GeneralOpenEndedApplicationQuery(
          List(
            MatchManyStatesQP(NonEmptyList.one(State.PRE_PRODUCTION) ++ List(State.PRODUCTION, State.PENDING_GATEKEEPER_APPROVAL))
          )
        ),
        Map(
          ParamNames.Status -> Seq("PRE_PRODUCTION", "PRODUCTION", "PENDING_GATEKEEPER_CHECK")
        )
      )
    }
  }

  "applicationsByApiContext" should {
    "convert to query" in {
      test(ApplicationQueries.applicationsByApiContext(apiContextOne), ParamNames.ApiContext -> s"$apiContextOne")
    }
  }

  "applicationsByApiIdentifier" should {
    "convert to query" in {
      test(
        ApplicationQueries.applicationsByApiIdentifier(apiIdentifierOne),
        ParamNames.ApiContext    -> s"${apiIdentifierOne.context}",
        ParamNames.ApiVersionNbr -> s"${apiIdentifierOne.versionNbr}"
      )
    }
  }

  "QueryParamsToQueryBuilder" should {
    "convert ById to query" should {
      test(ApplicationQuery.ById(applicationIdOne, Nil, false, false, false), ParamNames.ApplicationId -> s"$applicationIdOne")

      test(
        ApplicationQuery.ById(applicationIdOne, Nil, true, true, true),
        Map(
          ParamNames.ApplicationId          -> Seq(s"$applicationIdOne"),
          ParamNames.WantSubscriptions      -> Seq.empty,
          ParamNames.WantSubscriptionFields -> Seq.empty,
          ParamNames.WantStateHistory       -> Seq.empty
        )
      )
      test(
        ApplicationQuery.ById(applicationIdOne, Nil, true, false, false),
        Map(
          ParamNames.ApplicationId     -> Seq(s"$applicationIdOne"),
          ParamNames.WantSubscriptions -> Seq.empty
        )
      )
      test(
        ApplicationQuery.ById(applicationIdOne, Nil, false, true, false),
        Map(
          ParamNames.ApplicationId          -> Seq(s"$applicationIdOne"),
          ParamNames.WantSubscriptionFields -> Seq.empty
        )
      )
    }
    "convert ByClientId to query" should {
      test(ApplicationQuery.ByClientId(clientIdOne, false, Nil, false, false, false), ParamNames.ClientId -> s"$clientIdOne")
      test(
        ApplicationQuery.ByClientId(clientIdOne, false, Nil, true, true, true),
        Map(
          ParamNames.ClientId               -> Seq(s"$clientIdOne"),
          ParamNames.WantSubscriptions      -> Seq.empty,
          ParamNames.WantSubscriptionFields -> Seq.empty,
          ParamNames.WantStateHistory       -> Seq.empty
        )
      )
    }
    "convert ServerToken to query" should {
      test(ApplicationQuery.ByServerToken("bob", false, Nil, false, false, false), ParamNames.ServerToken -> "bob")
      test(
        ApplicationQuery.ByServerToken("bob", false, Nil, true, true, true),
        Map(
          ParamNames.ServerToken            -> Seq("bob"),
          ParamNames.WantSubscriptions      -> Seq.empty,
          ParamNames.WantSubscriptionFields -> Seq.empty,
          ParamNames.WantStateHistory       -> Seq.empty
        )
      )
    }

    "convert GenericUserAgentQP to query" in {
      testGOEAQ(List(GenericUserAgentQP("bob")))()
    }
    "convert NoSubscriptionsQP to query" in {
      testGOEAQOfNoValue(List(NoSubscriptionsQP))(ParamNames.NoSubscriptions)
    }
    "convert HasSubscriptionsQP to query" in {
      testGOEAQOfNoValue(List(HasSubscriptionsQP))(ParamNames.HasSubscriptions)
    }
    "convert ApiContextQP to query" in {
      testGOEAQ(List(ApiContextQP(apiContextOne)))(ParamNames.ApiContext -> s"$apiContextOne")
    }
    "convert ApiVersionNbrQP to query" in {
      testGOEAQ(List(ApiVersionNbrQP(apiVersionNbrOne)))(ParamNames.ApiVersionNbr -> s"$apiVersionNbrOne")
    }
    "convert LastUsedAfterQP to query" in {
      testGOEAQ(List(LastUsedAfterQP(instant)))(ParamNames.LastUsedAfter -> nowAsText)
    }
    "convert LastUsedBeforeQP to query" in {
      testGOEAQ(List(LastUsedBeforeQP(instant)))(ParamNames.LastUsedBefore -> nowAsText)
    }
    "convert UserIdQP to query" in {
      testGOEAQ(List(UserIdQP(userIdOne)))(ParamNames.UserId -> s"$userIdOne")
    }
    "convert EnvironmentQP to query" in {
      testGOEAQ(List(EnvironmentQP(Environment.SANDBOX)))(ParamNames.Environment -> "SANDBOX")
    }
    "convert IncludeDeletedQP to query" in {
      testGOEAQOfNoValue(List(IncludeDeletedQP))(ParamNames.IncludeDeleted)
    }
    "convert NoRestrictionQP to query" in {
      testGOEAQ(List(NoRestrictionQP))(ParamNames.DeleteRestriction -> "NO_RESTRICTION")
    }
    "convert DoNotDeleteQP to query" in {
      testGOEAQ(List(DoNotDeleteQP))(ParamNames.DeleteRestriction -> "DO_NOT_DELETE")
    }
    "convert ActiveStateQP to query" in {
      testGOEAQ(List(ActiveStateQP))(ParamNames.Status -> "ACTIVE")
    }
    "convert ExcludeDeletedQP to query" in {
      testGOEAQ(List(ExcludeDeletedQP))(ParamNames.Status -> "EXCLUDING_DELETED")
    }
    "convert BlockedStateQP to query" in {
      testGOEAQ(List(BlockedStateQP))(ParamNames.Status -> "BLOCKED")
    }
    "convert NoStateFilteringQP to query" in {
      testGOEAQ(List(NoStateFilteringQP))(ParamNames.Status -> "ANY")
    }
    "convert MatchAccessTypeQP(value) to query" in {
      testGOEAQ(List(MatchAccessTypeQP(AccessType.STANDARD)))(ParamNames.AccessType -> "STANDARD")
    }
    "convert MatchOneStateQP(value) to query" in {
      testGOEAQ(List(MatchOneStateQP(State.PRODUCTION)))(ParamNames.Status -> "PRODUCTION")
    }
    "convert MatchOneStateQP(PENDING_RI) to query" in {
      testGOEAQ(List(MatchOneStateQP(State.PENDING_REQUESTER_VERIFICATION)))(ParamNames.Status -> "PENDING_SUBMITTER_VERIFICATION")
    }
    "convert MatchManyStatesQP(value) to query" in {
      testGOEAQMap(List(MatchManyStatesQP(NonEmptyList.of(State.PRODUCTION, State.TESTING))))(Map(ParamNames.Status -> Seq("PRODUCTION", "CREATED")))
    }
    "convert AppStateBeforeDateQP(value) to query" in {
      testGOEAQ(List(AppStateBeforeDateQP(instant)))(ParamNames.StatusDateBefore -> nowAsText)
    }
    "convert SearchTextQP(value) to query" in {
      testGOEAQ(List(SearchTextQP("bob")))(ParamNames.Search -> "bob")
    }
    "convert NameQP(value) to query" in {
      testGOEAQ(List(NameQP("bob")))(ParamNames.Name -> "bob")
    }
    "convert VerificationCodeQP to query" in {
      testGOEAQ(List(VerificationCodeQP("bob")))(ParamNames.VerificationCode -> "bob")
    }
    "convert AnyAccessTypeQP to query" in {
      testGOEAQ(List(AnyAccessTypeQP))(ParamNames.AccessType -> "ANY")
    }
    "convert UserIdsQP to query" in {
      val users = List(userIdOne, userIdTwo)
      testGOEAQ(List(UserIdsQP(users)))(ParamNames.UserIds -> users.map(_.toString).mkString(","))
    }
    "convert UserIdQP with pagination to query" in {
      test(PaginatedApplicationQuery(List(UserIdQP(userIdOne)), Sorting.NoSorting, Pagination()), ParamNames.UserId -> s"$userIdOne", ParamNames.PageNbr -> "1")
    }
  }

  "paramForSorting" should {
    "convert to blank on no sort" in {
      QueryParamsToQueryStringMap.paramForSorting(Sorting.NoSorting) shouldBe Map.empty
    }

    "convert to label" in {
      QueryParamsToQueryStringMap.paramForSorting(Sorting.NameAscending) shouldBe Map(ParamNames.Sort -> Seq("NAME_ASC"))
    }
  }

  "paramsForPagination" should {
    import Pagination.Defaults

    "convert to query" in {
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(Defaults.PageSize, Defaults.PageNbr)) shouldBe Map(ParamNames.PageNbr -> Seq("1"))
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(Defaults.PageSize, 5)) shouldBe Map(ParamNames.PageNbr -> Seq("5"))
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(20, Defaults.PageNbr)) shouldBe Map(ParamNames.PageSize -> Seq("20"))
      QueryParamsToQueryStringMap.paramsForPagination(Pagination(20, 5)) shouldBe Map(ParamNames.PageSize -> Seq("20"), ParamNames.PageNbr -> Seq("5"))
    }
  }
}
