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

package uk.gov.hmrc.apiplatform.modules.applications.query.domain.models

import org.scalatest.EitherValues
import org.scalatest.prop.TableDrivenPropertyChecks

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApiIdentifierFixtures
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationWithCollaboratorsFixtures
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.ApplicationQuery._
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param._
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models._

class ApplicationQuerySpec extends HmrcSpec with ApplicationWithCollaboratorsFixtures with EitherValues with TableDrivenPropertyChecks with ApiIdentifierFixtures {

  "hasAnySubscriptionFilter" should {
    "return appropriate result for filters" in {
      val tests = Table(
        ("List of params", "Expected result"),
        (List.empty[Param[_]], false),
        (List(SortQP(Sorting.LastUseDateAscending)), false),
        (List(NoSubscriptionsQP), true),
        (List(HasSubscriptionsQP), true),
        (List(ApiContextQP(apiContextOne)), true),
        (List(ApiVersionNbrQP(apiVersionNbrOne)), true)
      )

      forAll(tests) {
        case (params, expected) => ApplicationQuery.hasAnySubscriptionFilter(params) shouldBe expected
      }
    }
  }

  "hasSpecificSubscriptionFilter" should {
    "return appropriate result for filters" in {
      val tests = Table(
        ("List of params", "Expected result"),
        (List.empty[Param[_]], false),
        (List(SortQP(Sorting.LastUseDateAscending)), false),
        (List(NoSubscriptionsQP), false),
        (List(HasSubscriptionsQP), false),
        (List(ApiContextQP(apiContextOne)), false),
        (List(ApiVersionNbrQP(apiVersionNbrOne)), true)
      )

      forAll(tests) {
        case (params, expected) => ApplicationQuery.hasSpecificSubscriptionFilter(params) shouldBe expected
      }
    }
  }

  "attemptToConstructQuery" should {
    def test(ps: List[Param[_]])(expectedQry: ApplicationQuery)(expectedLog: String): Unit = {
      val actualQry = ApplicationQuery.attemptToConstructQuery(ps)
      actualQry shouldBe expectedQry
      actualQry.asLogText shouldBe expectedLog
    }

    "work when given a correct applicationId" in {
      test(
        List(ApplicationIdQP(applicationIdOne))
      )(
        ApplicationQuery.ById(applicationIdOne, List.empty)
      )(
        "SingleApplicationQuery(ApplicationIdQP(???))"
      )
    }

    "work when given a correct clientId" in {
      test(
        List(ClientIdQP(clientIdOne))
      )(
        ApplicationQuery.ByClientId(clientIdOne, false, List.empty)
      )(
        "SingleApplicationQuery(ClientIdQP(???))"
      )
    }

    "work when given a server token" in {
      test(
        List(ServerTokenQP("bob"))
      )(
        ApplicationQuery.ByServerToken("bob", false, List.empty)
      )(
        "SingleApplicationQuery(ServerTokenQP(???))"
      )
    }

    "work when given a correct clientId and User Agent" in {
      test(List(ClientIdQP(clientIdOne), ApiGatewayUserAgentQP))(ApplicationQuery.ByClientId(
        clientIdOne,
        true,
        List(ApiGatewayUserAgentQP),
        false,
        false,
        false
      ))(
        "SingleApplicationQuery(ClientIdQP(???),ApiGatewayUserAgentQP)"
      )
      test(
        List(ClientIdQP(clientIdOne), GenericUserAgentQP("Bob"))
      )(
        ApplicationQuery.ByClientId(clientIdOne, false, List(GenericUserAgentQP("Bob")))
      )(
        "SingleApplicationQuery(ClientIdQP(???),GenericUserAgentQP(Bob))"
      )
    }

    "work when given a correct serverToken" in {
      test(
        List(ServerTokenQP("abc"))
      )(
        ApplicationQuery.ByServerToken("abc", false, List.empty)
      )(
        "SingleApplicationQuery(ServerTokenQP(???))"
      )
    }

    "work when given a correct serverToken and User Agent" in {
      test(
        List(ServerTokenQP("abc"), ApiGatewayUserAgentQP)
      )(
        ApplicationQuery.ByServerToken(
          "abc",
          true,
          List(ApiGatewayUserAgentQP)
        )
      )(
        "SingleApplicationQuery(ServerTokenQP(???),ApiGatewayUserAgentQP)"
      )
      test(
        List(ServerTokenQP("abc"), GenericUserAgentQP("Bob"))
      )(
        ApplicationQuery.ByServerToken(
          "abc",
          false,
          List(GenericUserAgentQP("Bob"))
        )
      )(
        "SingleApplicationQuery(ServerTokenQP(???),GenericUserAgentQP(Bob))"
      )
    }

    "work when given a correct applicationId and some irrelevant header" in {
      test(
        List(ApplicationIdQP(applicationIdOne), GenericUserAgentQP("XYZ"))
      )(
        ApplicationQuery.ById(
          applicationIdOne,
          List(GenericUserAgentQP("XYZ"))
        )
      )(
        "SingleApplicationQuery(ApplicationIdQP(???),GenericUserAgentQP(XYZ))"
      )
    }
    "work when given sorting and userId" in {
      test(
        List(UserIdQP(userIdOne), SortQP(Sorting.NameAscending))
      )(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), Sorting.NameAscending)
      )(
        "GeneralOpenEndedApplicationQuery(UserIdQP(???), sort=NameAscending)"
      )
    }

    "work when given sorting and userId and all wants" in {
      test(
        List(UserIdQP(userIdOne), SortQP(Sorting.NameAscending), WantSubscriptionsQP, WantSubscriptionFieldsQP, WantStateHistoryQP)
      )(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), Sorting.NameAscending, true, true, true)
      )(
        "GeneralOpenEndedApplicationQuery(UserIdQP(???), sort=NameAscending, wantSubscriptions, wantSubscriptionFields, wantStateHistory)"
      )
    }

    "work when given sorting and userId and one wants" in {
      test(
        List(UserIdQP(userIdOne), SortQP(Sorting.NameAscending), WantStateHistoryQP)
      )(
        GeneralOpenEndedApplicationQuery(List(UserIdQP(userIdOne)), Sorting.NameAscending, false, false, true)
      )(
        "GeneralOpenEndedApplicationQuery(UserIdQP(???), sort=NameAscending, wantStateHistory)"
      )
    }

    "work when given pagination, sorting and userId" in {
      test(
        List(UserIdQP(userIdOne), PageNbrQP(2), PageSizeQP(10), SortQP(Sorting.NameAscending))
      )(
        PaginatedApplicationQuery(
          List(UserIdQP(userIdOne)),
          Sorting.NameAscending,
          Pagination(10, 2)
        )
      )(
        "PaginatedApplicationQuery(UserIdQP(???), sort=NameAscending, pageNbr=2, pageSize=10)"
      )
    }

    "work when given page size, sorting and userId" in {
      test(
        List(UserIdQP(userIdOne), PageSizeQP(10), SortQP(Sorting.NameAscending))
      )(
        PaginatedApplicationQuery(
          List(UserIdQP(userIdOne)),
          Sorting.NameAscending,
          Pagination(10, Pagination.Defaults.PageNbr)
        )
      )(
        "PaginatedApplicationQuery(UserIdQP(???), sort=NameAscending, pageNbr=1, pageSize=10)"
      )
    }

    "work when given page nbr, sorting and userId" in {
      test(
        List(UserIdQP(userIdOne), PageNbrQP(2), SortQP(Sorting.NameAscending))
      )(
        PaginatedApplicationQuery(
          List(UserIdQP(userIdOne)),
          Sorting.NameAscending,
          Pagination(Pagination.Defaults.PageSize, 2)
        )
      )(
        "PaginatedApplicationQuery(UserIdQP(???), sort=NameAscending, pageNbr=2, pageSize=50)"
      )
    }
  }
}
