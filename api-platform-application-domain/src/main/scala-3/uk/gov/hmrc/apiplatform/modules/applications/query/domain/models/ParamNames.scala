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

enum ParamName(val text: String) {
  case WantSubscriptions      extends ParamName("wantSubscriptions")
  case WantSubscriptionFields extends ParamName("wantSubscriptionFields")
  case WantStateHistory       extends ParamName("wantStateHistory")
  //
  case Streamed               extends ParamName("streamed")
  //
  case Limit                  extends ParamName("limit")
  //
  case ServerToken            extends ParamName("serverToken")
  case ClientId               extends ParamName("clientId")
  case ApplicationId          extends ParamName("applicationId")
  //
  case PageNbr                extends ParamName("pageNbr")
  case PageSize               extends ParamName("pageSize")
  //
  case Sort                   extends ParamName("sort")
  //
  case NoSubscriptions        extends ParamName("noSubscriptions")
  case HasSubscriptions       extends ParamName("oneOrMoreSubscriptions")
  case ApiContext             extends ParamName("context")
  case ApiVersionNbr          extends ParamName("versionNbr")
  //
  case LastUsedAfter          extends ParamName("lastUsedAfter")
  case LastUsedBefore         extends ParamName("lastUsedBefore")
  case NeverUsed              extends ParamName("neverUsed")
  //
  case UserId                 extends ParamName("userId")
  case AdminUserId            extends ParamName("adminUserId")
  case UserIds                extends ParamName("userIds")
  //
  case Environment            extends ParamName("environment")
  //
  case IncludeDeleted         extends ParamName("includeDeleted")
  case DeleteRestriction      extends ParamName("deleteRestriction")
  //
  case Status                 extends ParamName("status")
  case StatusDateBefore       extends ParamName("statusDate")
  //
  case Search                 extends ParamName("search")
  case Name                   extends ParamName("name")
  case VerificationCode       extends ParamName("verificationCode")
  //
  case AccessType             extends ParamName("accessType")
  //
  case OrganisationId         extends ParamName("organisationId")
}
