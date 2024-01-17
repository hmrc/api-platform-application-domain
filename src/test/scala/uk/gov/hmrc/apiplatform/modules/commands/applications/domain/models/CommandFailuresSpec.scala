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

package uk.gov.hmrc.apiplatform.modules.commands.applications.domain.models

import play.api.libs.json.Json
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec

class CommandFailuresSpec extends HmrcSpec {

  def testCommandFailure(jsonText: String, commandFailure: CommandFailure) = {
    Json.parse(jsonText).as[CommandFailure] shouldBe commandFailure

    val jsonVal = Json.toJson[CommandFailure](commandFailure)
    jsonVal shouldBe Json.parse(jsonText)
  }

  "A CommandFailure" should {
    "toString" in {
      val failure = CommandFailures.InsufficientPrivileges("Bob is not an alien")
      failure.toString() shouldBe "Insufficient privileges - Bob is not an alien"
    }

    "handle json for ApplicationNotFound" in {
      testCommandFailure("""{"failureType":"ApplicationNotFound"}""", CommandFailures.ApplicationNotFound)
    }

    "handle json for InsufficientPrivileges" in {
      testCommandFailure("""{"failureType":"InsufficientPrivileges", "details": "Bob is not an alien"}""", CommandFailures.InsufficientPrivileges("Bob is not an alien"))
    }

    "handle json for CannotRemoveLastAdmin" in {
      testCommandFailure("""{"failureType":"CannotRemoveLastAdmin"}""", CommandFailures.CannotRemoveLastAdmin)
    }

    "handle json for ActorIsNotACollaboratorOnApp" in {
      testCommandFailure("""{"failureType":"ActorIsNotACollaboratorOnApp"}""", CommandFailures.ActorIsNotACollaboratorOnApp)
    }

    "handle json for ClientSecretLimitExceeded" in {
      testCommandFailure("""{"failureType":"ClientSecretLimitExceeded"}""", CommandFailures.ClientSecretLimitExceeded)
    }

    "handle json for CollaboratorDoesNotExistOnApp" in {
      testCommandFailure("""{"failureType":"CollaboratorDoesNotExistOnApp"}""", CommandFailures.CollaboratorDoesNotExistOnApp)
    }

    "handle json for CollaboratorHasMismatchOnApp" in {
      testCommandFailure("""{"failureType":"CollaboratorHasMismatchOnApp"}""", CommandFailures.CollaboratorHasMismatchOnApp)
    }

    "handle json for CollaboratorAlreadyExistsOnApp" in {
      testCommandFailure("""{"failureType":"CollaboratorAlreadyExistsOnApp"}""", CommandFailures.CollaboratorAlreadyExistsOnApp)
    }

    "handle json for DuplicateSubscription" in {
      testCommandFailure("""{"failureType":"DuplicateSubscription"}""", CommandFailures.DuplicateSubscription)
    }

    "handle json for SubscriptionNotAvailable" in {
      testCommandFailure("""{"failureType":"SubscriptionNotAvailable"}""", CommandFailures.SubscriptionNotAvailable)
    }

    "handle json for NotSubscribedToApi" in {
      testCommandFailure("""{"failureType":"NotSubscribedToApi"}""", CommandFailures.NotSubscribedToApi)
    }

    "handle json for GenericFailure" in {
      testCommandFailure("""{"describe":"someError","failureType":"GenericFailure"}""", CommandFailures.GenericFailure("someError"))
    }

  }
}
