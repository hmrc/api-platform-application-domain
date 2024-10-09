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

package uk.gov.hmrc.apiplatform.modules.applications.core.domain.models

import org.scalatest.matchers.should.Matchers

import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.{Access, AccessFixtures}
import uk.gov.hmrc.apiplatform.modules.applications.submissions.domain.models.{PrivacyPolicyLocations, TermsAndConditionsLocations}

class HasAccessSpec extends HmrcSpec with Matchers with AccessFixtures {

  case class TestFixture(access: Access) extends HasAccess

  "hasAccess" when {
    "Standard Access" should {
      val objInTest = TestFixture(standardAccessOne)

      "isStandard should be true, isPrivileged & isROPC should be false" in {
        objInTest.isStandard shouldBe true
        objInTest.isPrivileged shouldBe false
        objInTest.isROPC shouldBe false
      }

      "privacyPolicyLocation should return value when set" in {
        objInTest.privacyPolicyLocation shouldBe standardAccessOne.privacyPolicyUrl.map(PrivacyPolicyLocations.Url(_))
      }
      "termsAndConditionsLocation should return value when set" in {
        objInTest.termsAndConditionsLocation shouldBe standardAccessOne.termsAndConditionsUrl.map(TermsAndConditionsLocations.Url(_))
      }
    }

    "ROPC Access" should {
      val objInTest = TestFixture(Access.Ropc())
      "isROPC should be true, isPrivileged & isStandard should be false" in {
        objInTest.isROPC shouldBe true
        objInTest.isStandard shouldBe false
        objInTest.isPrivileged shouldBe false
      }
      "privacyPolicyLocation should return value when set" in {
        objInTest.privacyPolicyLocation shouldBe None
      }
      "termsAndConditionsLocation should return value when set" in {
        objInTest.termsAndConditionsLocation shouldBe None
      }
    }

    "Priviledged Access" should {
      val objInTest = TestFixture(Access.Ropc())
      "isPrivileged should be true, isROPC & isStandard should be false" in {
        objInTest.isROPC shouldBe true
        objInTest.isStandard shouldBe false
        objInTest.isPrivileged shouldBe false
      }
      "privacyPolicyLocation should return value when set" in {
        objInTest.privacyPolicyLocation shouldBe None
      }
      "termsAndConditionsLocation should return value when set" in {
        objInTest.termsAndConditionsLocation shouldBe None
      }
    }
  }

}
