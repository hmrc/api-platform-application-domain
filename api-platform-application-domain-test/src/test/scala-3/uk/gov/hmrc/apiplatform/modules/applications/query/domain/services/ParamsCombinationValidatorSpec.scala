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
import cats.data.Validated.Invalid
import cats.syntax.validated.*
import org.scalatest.EitherValues
import org.scalatest.compatible.Assertion

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiIdentifierFixtures, Environment}
import uk.gov.hmrc.apiplatform.modules.common.utils.{FixedClock, HmrcSpec}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.{ApplicationWithCollaboratorsFixtures, State}
import uk.gov.hmrc.apiplatform.modules.applications.query.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.{NonUniqueFilterParam, Param, UniqueFilterParam}

class ParamsCombinationValidatorSpec
    extends HmrcSpec
    with ApplicationWithCollaboratorsFixtures
    with ApiIdentifierFixtures
    with EitherValues
    with FixedClock {

  val Pass = ().validNel

  def shouldFail[T](testThis: ErrorsOr[T]): Assertion = {
    inside(testThis) {
      case Invalid(_) => succeed
      case _          => fail("valid when expecting invalid")
    }
  }

  "checWants" should {
    val test: (List[Param[_]]) => ErrorsOr[Unit] = (ps) => ParamsCombinationValidator.validateParamCombinations(using ps)
    "disallow wantSubscriptionFields with anything other than a single app query" in {
      test(List(WantSubscriptionFieldsQP, PageNbrQP(1))) should not be Pass
      test(List(WantSubscriptionFieldsQP, UserIdQP(userIdOne))) should not be Pass
      test(List(WantSubscriptionFieldsQP, ApplicationIdQP(applicationIdOne))) shouldBe Pass
      test(List(WantSubscriptionFieldsQP, ClientIdQP(clientIdOne))) shouldBe Pass
      test(List(WantSubscriptionFieldsQP, ServerTokenQP("abc"))) shouldBe Pass
    }
    "disallow streamed with a single app query" in {
      test(List(StreamedQP, PageNbrQP(1))) shouldBe Pass
      test(List(StreamedQP, UserIdQP(userIdOne))) shouldBe Pass
      test(List(StreamedQP, ApplicationIdQP(applicationIdOne))) should not be Pass
      test(List(StreamedQP, ClientIdQP(clientIdOne))) should not be Pass
      test(List(StreamedQP, ServerTokenQP("abc"))) should not be Pass
    }

  }

  "checkLastUsedParamsCombinations" should {

    val test = (ps: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkLastUsedParamsCombinations(ps)

    "pass when provided no last active dates" in {
      test(List.empty) shouldBe Pass
    }

    "pass when provided a last active before date" in {
      test(List(LastUsedAfterQP(instant))) shouldBe Pass
    }

    "pass when provided a last active after date" in {
      test(List(LastUsedAfterQP(instant))) shouldBe Pass
    }

    "pass when provided sensible last active before and last active after dates" in {
      test(List(LastUsedAfterQP(instant.minusSeconds(5)), LastUsedBeforeQP(instant))) shouldBe Pass
    }

    "fail when provided incorrect last active before and last active after dates" in {
      test(List(LastUsedAfterQP(instant), LastUsedBeforeQP(instant.minusSeconds(5)))) shouldBe "Cannot query for used after date that is after a given before date".invalidNel
    }
  }

  "checkUserCombinations" should {
    val test: List[NonUniqueFilterParam[_]] => ErrorsOr[Unit] = (ps) => ParamsCombinationValidator.checkUserCombinations(ps)
    val user                                                  = UserIdQP(userIdOne)
    val admin                                                 = AdminUserIdQP(userIdOne)
    val users                                                 = UserIdsQP(List(userIdOne, userIdTwo))
    val error                                                 = "Cannot query with multiple user id based filters".invalidNel

    "pass when given no user based params" in {
      test(List(NoSubscriptionsQP)) shouldBe Pass
    }

    "pass when given one user based params" in {
      test(List(user)) shouldBe Pass
      test(List(admin)) shouldBe Pass
      test(List(users)) shouldBe Pass
    }

    "fail when given more than one user based params" in {
      test(List(user, admin)) shouldBe error
      test(List(user, users)) shouldBe error
      test(List(admin, users)) shouldBe error
    }
  }
  "checkSubscriptionsParamsCombinations" should {
    val test: List[NonUniqueFilterParam[_]] => ErrorsOr[Unit] = (ps) => ParamsCombinationValidator.checkSubscriptionsParamsCombinations(ps)
    val Context                                               = ApiContextQP(apiContextOne)
    val Version                                               = ApiVersionNbrQP(apiVersionNbrOne)

    "pass when given valid combinations" in {
      test(List(NoSubscriptionsQP)) shouldBe Pass
      test(List(HasSubscriptionsQP)) shouldBe Pass
      test(List(Context)) shouldBe Pass
      test(List(Context, Version)) shouldBe Pass
      test(List.empty) shouldBe Pass
    }

    "fail when given invalid combinations" in {
      shouldFail(test(List(NoSubscriptionsQP, HasSubscriptionsQP)))
      shouldFail(test(List(NoSubscriptionsQP, Context)))
      shouldFail(test(List(NoSubscriptionsQP, Context, Version)))
      shouldFail(test(List(NoSubscriptionsQP, Version)))
      shouldFail(test(List(HasSubscriptionsQP, Context)))
      shouldFail(test(List(HasSubscriptionsQP, Context, Version)))
      shouldFail(test(List(HasSubscriptionsQP, Version)))
      shouldFail(test(List(Version)))
    }
  }

  "checkUniqueParamsCombinations" should {
    val testBadCombo  =
      (us: NonEmptyList[UniqueFilterParam[_]], os: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkUniqueParamsCombinations(us, os) should not be Pass
    val testGoodCombo =
      (us: NonEmptyList[UniqueFilterParam[_]], os: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkUniqueParamsCombinations(us, os) shouldBe Pass

    "pass when given a correct applicationId" in {
      testGoodCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne)), List.empty)
    }

    "pass when given a correct clientId" in {
      testGoodCombo(NonEmptyList.of(ClientIdQP(clientIdOne)), List.empty)
    }

    "pass when given a correct serverToken" in {
      testGoodCombo(NonEmptyList.of(ServerTokenQP("abc")), List.empty)
    }

    "pass when given a correct clientId and User Agent" in {
      testGoodCombo(NonEmptyList.of(ClientIdQP(clientIdOne)), List(ApiGatewayUserAgentQP))
      testGoodCombo(NonEmptyList.of(ClientIdQP(clientIdOne)), List(GenericUserAgentQP("Bob")))
    }

    "pass when given a correct serverToken and User Agent" in {
      testGoodCombo(NonEmptyList.of(ServerTokenQP("abc")), List(ApiGatewayUserAgentQP))
      testGoodCombo(NonEmptyList.of(ServerTokenQP("abc")), List(GenericUserAgentQP("Bob")))
    }

    "pass when given a correct applicationId and some irrelevant header" in {
      testGoodCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne)), List(GenericUserAgentQP("XYZ")))
    }

    "fail when mixing two ids" in {
      testBadCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne), ClientIdQP(clientIdOne)), List.empty)
      testBadCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne), ServerTokenQP("ABC")), List.empty)
      testBadCombo(NonEmptyList.of(ClientIdQP(clientIdOne), ServerTokenQP("ABC")), List.empty)
    }

    "pass when mixing unique and appropriate non unqiue params" in {
      testGoodCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne)), List(EnvironmentQP(Environment.Production)))
      testGoodCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne)), List(GenericUserAgentQP("xxx")))
      testGoodCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne)), List(ApiGatewayUserAgentQP))
    }

    "fail when mixing unique and inappropriate non unqiue params" in {
      testBadCombo(NonEmptyList.of(ApplicationIdQP(applicationIdOne)), List(AdminUserIdQP(userIdOne)))
    }

  }

  "checkVerificationCodeUsesDeleteExclusion" should {
    val testBadCombo  = (ps: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkVerificationCodeUsesDeleteExclusion(ps) should not be Pass
    val testGoodCombo = (ps: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkVerificationCodeUsesDeleteExclusion(ps) shouldBe Pass

    "pass when given a verification code and exclude deleted" in {
      testGoodCombo(List(ExcludeDeletedQP, VerificationCodeQP("ABC")))
    }
    "fail when given a verification code only" in {
      testBadCombo(List(VerificationCodeQP("ABC")))
    }
    "fail when given a verification code and a state filter" in {
      testBadCombo(List(ActiveStateQP, VerificationCodeQP("ABC")))
    }
  }

  "checkAppStateFilters" should {
    val oneState     = MatchOneStateQP(State.Production)
    val manyState    = MatchManyStatesQP(NonEmptyList.of(State.Production, State.PreProduction))
    val blockedState = BlockedStateQP
    val dateBefore   = uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.AppStateBeforeDateQP(instant)

    val testBadCombo  = (ps: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkAppStateFilters(ps) should not be Pass
    val testGoodCombo = (ps: List[NonUniqueFilterParam[_]]) => ParamsCombinationValidator.checkAppStateFilters(ps) shouldBe Pass

    "pass when only a state filter" in {
      testGoodCombo(oneState :: Nil)
      testGoodCombo(manyState :: Nil)
      testGoodCombo(blockedState :: Nil)
    }

    "pass when one state with a date before" in {
      testGoodCombo(oneState :: dateBefore :: Nil)
    }

    "fail when no state but with a date before" in {
      testBadCombo(dateBefore :: Nil)
    }

    "fail when inappropriate state with a date before" in {
      testBadCombo(manyState :: dateBefore :: Nil)
      testBadCombo(blockedState :: dateBefore :: Nil)
    }
  }

  "checkLimit" should {
    val test: (List[Param[_]]) => ErrorsOr[Unit] = (ps) => ParamsCombinationValidator.validateParamCombinations(using ps)
    "pass when used with an open ended query" in {
      test(List(LimitQP(50), BlockedStateQP)) shouldBe Pass
    }
    "fail when used with a single app query" in {
      test(List(LimitQP(50), ClientIdQP(clientIdOne))) should not be Pass
    }
    "fail when used with a paginated query" in {
      test(List(LimitQP(50), PageNbrQP(1))) should not be Pass
    }
  }
}
