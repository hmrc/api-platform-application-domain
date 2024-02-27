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

package uk.gov.hmrc.apiplatform.modules.submissions.domain.models

import play.api.libs.json._
import uk.gov.hmrc.apiplatform.modules.common.utils.BaseJsonFormattersSpec

import uk.gov.hmrc.apiplatform.modules.submissions.utils.SubmissionsTestData

class SubmissionSpec extends BaseJsonFormattersSpec with SubmissionsTestData {

  import Submission.extendedSubmissionFormat

  def jsonSubmission(questionnaireState: String = "NotApplicable") = {
    s"""{
       |  "submission" : {
       |    "id" : "${partiallyAnsweredExtendedSubmission.submission.id}",
       |    "applicationId" : "${partiallyAnsweredExtendedSubmission.submission.applicationId}",
       |    "startedOn" : "2020-01-02T03:04:05.006Z",
       |    "groups" : [ {
       |      "heading" : "Your processes",
       |      "links" : [ {
       |        "id" : "796336a5-f7b4-4dad-8003-a818e342cbb4",
       |        "label" : "Development practices",
       |        "questions" : [ {
       |          "question" : {
       |            "id" : "653d2ee4-09cf-46a0-bc73-350a385ae860",
       |            "wording" : "Do your development practices follow our guidance?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "fragments" : [ {
       |                  "text" : "You must develop software following our",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "development practices (opens in a new tab)",
       |                  "url" : "https://developer.service.hmrc.gov.uk/api-documentation/docs/development-practices",
       |                  "statementType" : "link"
       |                }, {
       |                  "text" : ".",
       |                  "statementType" : "text"
       |                } ],
       |                "statementType" : "compound"
       |              } ]
       |            },
       |            "yesMarking" : "pass",
       |            "noMarking" : "warn",
       |            "questionType" : "yesNo"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "6139f57d-36ab-4338-85b3-a2079d6cf376",
       |            "wording" : "Does your error handling meet our specification?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "fragments" : [ {
       |                  "text" : "We will check for evidence that you comply with our",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "error handling specification (opens in new tab)",
       |                  "url" : "https://developer.service.hmrc.gov.uk/api-documentation/docs/reference-guide#errors",
       |                  "statementType" : "link"
       |                }, {
       |                  "text" : ".",
       |                  "statementType" : "text"
       |                } ],
       |                "statementType" : "compound"
       |              } ]
       |            },
       |            "yesMarking" : "pass",
       |            "noMarking" : "fail",
       |            "questionType" : "yesNo"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "3c5cd29d-bec2-463f-8593-cd5412fab1e5",
       |            "wording" : "Does your software meet accessibility standards?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "fragments" : [ {
       |                  "text" : "Web-based software must meet level AA of the",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "Web Content Accessibility Guidelines (WCAG) (opens in new tab)",
       |                  "url" : "https://www.w3.org/WAI/standards-guidelines/wcag/",
       |                  "statementType" : "link"
       |                }, {
       |                  "text" : ". Desktop software should follow equivalent offline standards.",
       |                  "statementType" : "text"
       |                } ],
       |                "statementType" : "compound"
       |              } ]
       |            },
       |            "yesMarking" : "pass",
       |            "noMarking" : "warn",
       |            "questionType" : "yesNo"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        } ]
       |      } ]
       |    }, {
       |      "heading" : "Your software",
       |      "links" : [ {
       |        "id" : "3a7f3369-8e28-447c-bd47-efbabeb6d93f",
       |        "label" : "Customers authorising your software",
       |        "questions" : [ {
       |          "question" : {
       |            "id" : "95da25e8-af3a-4e05-a621-4a5f4ca788f6",
       |            "wording" : "Customers authorising your software",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "Your customers will see the information you provide here when they authorise your software to interact with HMRC.",
       |                "statementType" : "text"
       |              }, {
       |                "text" : "Before you continue, you will need:",
       |                "statementType" : "text"
       |              }, {
       |                "bullets" : [ {
       |                  "text" : "the name of your software",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "the location of your servers which store customer data",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "a link to your privacy policy",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "a link to your terms and conditions",
       |                  "statementType" : "text"
       |                } ],
       |                "statementType" : "bullets"
       |              } ]
       |            },
       |            "questionType" : "acknowledgement"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "4d5a41c8-8727-4d09-96c0-e2ce1bc222d3",
       |            "wording" : "Confirm the name of your software",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "We show this name to your users when they authorise your software to interact with HMRC.",
       |                "statementType" : "text"
       |              }, {
       |                "fragments" : [ {
       |                  "text" : "It must comply with our ",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "naming guidelines (opens in a new tab)",
       |                  "url" : "https://developer.service.hmrc.gov.uk/api-documentation/docs/using-the-hub/name-guidelines",
       |                  "statementType" : "link"
       |                }, {
       |                  "text" : ".",
       |                  "statementType" : "text"
       |                } ],
       |                "statementType" : "compound"
       |              }, {
       |                "text" : "Application name",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "57d706ad-c0b8-462b-a4f8-90e7aa58e57a",
       |            "wording" : "Where are your servers that store customer information?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "Select all that apply.",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "marking" : [ {
       |              "In the UK" : "pass"
       |            }, {
       |              "In the European Economic Area (EEA)" : "pass"
       |            }, {
       |              "Outside the European Economic Area" : "warn"
       |            } ],
       |            "questionType" : "multi"
       |          },
       |          "askWhen" : {
       |            "contextKey" : "IN_HOUSE_SOFTWARE",
       |            "expectedValue" : "No",
       |            "askWhen" : "askWhenContext"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "b0ae9d71-e6a7-4cf6-abd4-7eb7ba992bc6",
       |            "wording" : "Do you have a privacy policy URL for your software?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "You need a privacy policy covering the software you request production credentials for.",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "marking" : [ {
       |              "Yes" : "pass"
       |            }, {
       |              "No" : "fail"
       |            }, {
       |              "The privacy policy is in desktop software" : "pass"
       |            } ],
       |            "questionType" : "choose"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "c0e4b068-23c9-4d51-a1fa-2513f50e428f",
       |            "wording" : "What is your privacy policy URL?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "For example https://example.com/privacy-policy",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "validation" : {
       |              "validationType" : "url"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "b0ae9d71-e6a7-4cf6-abd4-7eb7ba992bc6",
       |            "expectedValue" : {
       |              "value" : "Yes"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "ca6af382-4007-4228-a781-1446231578b9",
       |            "wording" : "Do you have a terms and conditions URL for your software?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "You need terms and conditions covering the software you request production credentials for.",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "marking" : [ {
       |              "Yes" : "pass"
       |            }, {
       |              "No" : "fail"
       |            }, {
       |              "The terms and conditions are in desktop software" : "pass"
       |            } ],
       |            "questionType" : "choose"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "0a6d6973-c49a-49c3-93ff-de58daa1b90c",
       |            "wording" : "What is your terms and conditions URL?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "For example https://example.com/terms-conditions",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "ca6af382-4007-4228-a781-1446231578b9",
       |            "expectedValue" : {
       |              "value" : "Yes"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        } ]
       |      } ]
       |    }, {
       |      "heading" : "Your details",
       |      "links" : [ {
       |        "id" : "ac69b129-524a-4d10-89a5-7bfa46ed95c7",
       |        "label" : "Organisation details",
       |        "questions" : [ {
       |          "question" : {
       |            "id" : "99d9362d-e365-4af1-aa46-88e95f9858f7",
       |            "wording" : "Are you the individual responsible for the software in your organisation?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "As the responsible individual you:",
       |                "statementType" : "text"
       |              }, {
       |                "bullets" : [ {
       |                  "fragments" : [ {
       |                    "text" : "ensure your software conforms to the ",
       |                    "statementType" : "text"
       |                  }, {
       |                    "text" : "terms of use (opens in new tab)",
       |                    "url" : "/api-documentation/docs/terms-of-use",
       |                    "statementType" : "link"
       |                  } ],
       |                  "statementType" : "compound"
       |                }, {
       |                  "fragments" : [ {
       |                    "text" : "understand the ",
       |                    "statementType" : "text"
       |                  }, {
       |                    "text" : "consequences of not conforming to the terms of use (opens in new tab)",
       |                    "url" : "/api-documentation/docs/terms-of-use",
       |                    "statementType" : "link"
       |                  } ],
       |                  "statementType" : "compound"
       |                } ],
       |                "statementType" : "bullets"
       |              } ]
       |            },
       |            "yesMarking" : "pass",
       |            "noMarking" : "pass",
       |            "errorInfo" : {
       |              "summary" : "Select Yes if you are the individual responsible for the software in your organisation"
       |            },
       |            "questionType" : "yesNo"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "36b7e670-83fc-4b31-8f85-4d3394908495",
       |            "wording" : "Who is responsible for the software in your organisation?",
       |            "label" : "First and last name",
       |            "errorInfo" : {
       |              "summary" : "Enter a first and last name",
       |              "message" : "First and last name cannot be blank"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "99d9362d-e365-4af1-aa46-88e95f9858f7",
       |            "expectedValue" : {
       |              "value" : "No"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "fb9b8036-cc88-4f4e-ad84-c02caa4cebae",
       |            "wording" : "Give us the email address of the individual responsible for the software",
       |            "afterStatement" : {
       |              "fragments" : [ {
       |                "text" : "We will email a verification link to the responsible individual that expires in 10 working days.",
       |                "statementType" : "text"
       |              }, {
       |                "text" : "The responsible individual must verify before we can process your request for production credentials.",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "label" : "Email address",
       |            "hintText" : {
       |              "text" : "Cannot be a shared mailbox",
       |              "statementType" : "text"
       |            },
       |            "validation" : {
       |              "validationType" : "email"
       |            },
       |            "errorInfo" : {
       |              "summary" : "Enter an email address in the correct format, like yourname@example.com",
       |              "message" : "Email address cannot be blank"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "99d9362d-e365-4af1-aa46-88e95f9858f7",
       |            "expectedValue" : {
       |              "value" : "No"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "b9dbf0a5-e72b-4c89-a735-26f0858ca6cc",
       |            "wording" : "What is your organisation’s URL?",
       |            "hintText" : {
       |              "text" : "For example https://example.com",
       |              "statementType" : "text"
       |            },
       |            "validation" : {
       |              "validationType" : "url"
       |            },
       |            "absence" : [ "My organisation doesn't have a website", "fail" ],
       |            "errorInfo" : {
       |              "summary" : "Enter a URL in the correct format, like https://example.com"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "wording" : "Identify your organisation",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "Provide evidence that you or your organisation is officially registered in the UK. Choose one option.",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "marking" : [ {
       |              "Unique Taxpayer Reference (UTR)" : "pass"
       |            }, {
       |              "VAT registration number" : "pass"
       |            }, {
       |              "Corporation Tax Unique Taxpayer Reference (UTR)" : "pass"
       |            }, {
       |              "PAYE reference" : "pass"
       |            }, {
       |              "My organisation is in the UK and doesn't have any of these" : "pass"
       |            }, {
       |              "My organisation is outside the UK and doesn't have any of these" : "warn"
       |            } ],
       |            "errorInfo" : {
       |              "summary" : "Select a way to identify your organisation"
       |            },
       |            "questionType" : "choose"
       |          },
       |          "askWhen" : {
       |            "askWhen" : "alwaysAsk"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "4e148791-1a07-4f28-8fe4-ba3e18cdc118",
       |            "wording" : "What is your company registration number?",
       |            "statement" : {
       |              "fragments" : [ {
       |                "fragments" : [ {
       |                  "text" : "You can ",
       |                  "statementType" : "text"
       |                }, {
       |                  "text" : "search Companies House for your company registration number (opens in new tab)",
       |                  "url" : "https://find-and-update.company-information.service.gov.uk/",
       |                  "statementType" : "link"
       |                }, {
       |                  "text" : ".",
       |                  "statementType" : "text"
       |                } ],
       |                "statementType" : "compound"
       |              } ]
       |            },
       |            "hintText" : {
       |              "text" : "It is 8 characters. For example, 01234567 or AC012345.",
       |              "statementType" : "text"
       |            },
       |            "absence" : [ "My organisation doesn't have a company registration", "fail" ],
       |            "errorInfo" : {
       |              "summary" : "Your company registration number cannot be blank",
       |              "message" : "Enter your company registration number, like 01234567"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "expectedValue" : {
       |              "value" : "My organisation is in the UK and doesn't have any of these"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "55da0b97-178c-45b5-a139-b61ad7b9ca84",
       |            "wording" : "What is your Self Assessment Unique Taxpayer Reference?",
       |            "hintText" : {
       |              "fragments" : [ {
       |                "text" : "This is 10 numbers, for example 1234567890. It will be on tax returns and other letters about Self Assessment. It may be called ‘reference’, ‘UTR’ or ‘official use’. You can ",
       |                "statementType" : "text"
       |              }, {
       |                "text" : "find a lost UTR number (opens in new tab)",
       |                "url" : "https://www.gov.uk/find-lost-utr-number",
       |                "statementType" : "link"
       |              }, {
       |                "text" : ".",
       |                "statementType" : "text"
       |              } ],
       |              "statementType" : "compound"
       |            },
       |            "errorInfo" : {
       |              "summary" : "Your Self Assessment Unique Taxpayer Reference cannot be blank",
       |              "message" : "Enter your Self Assessment Unique Taxpayer Reference, like 1234567890"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "expectedValue" : {
       |              "value" : "Unique Taxpayer Reference (UTR)"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "dd12fd8b-907b-4ba1-95d3-ef6317f36199",
       |            "wording" : "What is your company’s VAT registration number?",
       |            "hintText" : {
       |              "text" : "This is 9 numbers, sometimes with ‘GB’ at the start, for example 123456789 or GB123456789. You can find it on your company’s VAT registration certificate.",
       |              "statementType" : "text"
       |            },
       |            "errorInfo" : {
       |              "summary" : "Your company's VAT registration number cannot be blank",
       |              "message" : "Enter your company's VAT registration number, like 123456789"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "expectedValue" : {
       |              "value" : "VAT registration number"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "6be23951-ac69-47bf-aa56-86d3d690ee0b",
       |            "wording" : "What is your Corporation Tax Unique Taxpayer Reference?",
       |            "hintText" : {
       |              "fragments" : [ {
       |                "text" : "This is 10 numbers, for example 1234567890. It will be on tax returns and other letters about Corporation Tax. It may be called ‘reference’, ‘UTR’ or ‘official use’. You can ",
       |                "statementType" : "text"
       |              }, {
       |                "text" : "find a lost UTR number (opens in new tab)",
       |                "url" : "https://www.gov.uk/find-lost-utr-number",
       |                "statementType" : "link"
       |              }, {
       |                "text" : ".",
       |                "statementType" : "text"
       |              } ],
       |              "statementType" : "compound"
       |            },
       |            "errorInfo" : {
       |              "summary" : "Your Corporation Tax Unique Taxpayer Reference cannot be blank",
       |              "message" : "Enter your Corporation Tax Unique Taxpayer Reference, like 1234567890"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "expectedValue" : {
       |              "value" : "Corporation Tax Unique Taxpayer Reference (UTR)"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "a143760e-72f3-423b-a6b4-558db37a3453",
       |            "wording" : "What is your company’s employer PAYE reference?",
       |            "hintText" : {
       |              "text" : "This is a 3 digit tax office number, a forward slash, and a tax office employer reference, like 123/AB456. It may be called ‘Employer PAYE reference’ or ‘PAYE reference’. It will be on your P60.",
       |              "statementType" : "text"
       |            },
       |            "errorInfo" : {
       |              "summary" : "Your company's employer PAYE reference number cannot be blank",
       |              "message" : "Enter your company's employer PAYE reference number, like 123/AB456"
       |            },
       |            "questionType" : "text"
       |          },
       |          "askWhen" : {
       |            "questionId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "expectedValue" : {
       |              "value" : "PAYE reference"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        }, {
       |          "question" : {
       |            "id" : "a12f314e-bc12-4e0d-87ba-1326acb31008",
       |            "wording" : "Provide evidence of your organisation’s registration",
       |            "statement" : {
       |              "fragments" : [ {
       |                "text" : "You will need to provide evidence that your organisation is officially registered in a country outside of the UK.",
       |                "statementType" : "text"
       |              }, {
       |                "text" : "You will be asked for a digital copy of the official registration document.",
       |                "statementType" : "text"
       |              } ]
       |            },
       |            "questionType" : "acknowledgement"
       |          },
       |          "askWhen" : {
       |            "questionId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |            "expectedValue" : {
       |              "value" : "My organisation is outside the UK and doesn't have any of these"
       |            },
       |            "askWhen" : "askWhenAnswer"
       |          }
       |        } ]
       |      } ]
       |    } ],
       |    "questionIdsOfInterest" : {
       |      "applicationNameId" : "4d5a41c8-8727-4d09-96c0-e2ce1bc222d3",
       |      "privacyPolicyId" : "b0ae9d71-e6a7-4cf6-abd4-7eb7ba992bc6",
       |      "privacyPolicyUrlId" : "c0e4b068-23c9-4d51-a1fa-2513f50e428f",
       |      "termsAndConditionsId" : "ca6af382-4007-4228-a781-1446231578b9",
       |      "termsAndConditionsUrlId" : "0a6d6973-c49a-49c3-93ff-de58daa1b90c",
       |      "organisationUrlId" : "b9dbf0a5-e72b-4c89-a735-26f0858ca6cc",
       |      "responsibleIndividualIsRequesterId" : "99d9362d-e365-4af1-aa46-88e95f9858f7",
       |      "responsibleIndividualNameId" : "36b7e670-83fc-4b31-8f85-4d3394908495",
       |      "responsibleIndividualEmailId" : "fb9b8036-cc88-4f4e-ad84-c02caa4cebae",
       |      "identifyYourOrganisationId" : "cbdf264f-be39-4638-92ff-6ecd2259c662",
       |      "serverLocationsId" : "57d706ad-c0b8-462b-a4f8-90e7aa58e57a"
       |    },
       |    "instances" : [ {
       |      "index" : 0,
       |      "answersToQuestions" : {
       |        "fb9b8036-cc88-4f4e-ad84-c02caa4cebae" : {
       |          "value" : "bob@example.com",
       |          "answerType" : "text"
       |        },
       |        "36b7e670-83fc-4b31-8f85-4d3394908495" : {
       |          "value" : "Bob Cratchett",
       |          "answerType" : "text"
       |        },
       |        "4d5a41c8-8727-4d09-96c0-e2ce1bc222d3" : {
       |          "value" : "expectedAppName",
       |          "answerType" : "text"
       |        },
       |        "99d9362d-e365-4af1-aa46-88e95f9858f7" : {
       |          "value" : "No",
       |          "answerType" : "singleChoice"
       |        },
       |        "b0ae9d71-e6a7-4cf6-abd4-7eb7ba992bc6" : {
       |          "value" : "No",
       |          "answerType" : "singleChoice"
       |        },
       |        "ca6af382-4007-4228-a781-1446231578b9" : {
       |          "value" : "No",
       |          "answerType" : "singleChoice"
       |        },
       |        "57d706ad-c0b8-462b-a4f8-90e7aa58e57a" : {
       |          "values" : [ "In the UK", "Outside the EEA with adequacy agreements" ],
       |          "answerType" : "multipleChoice"
       |        }
       |      },
       |      "statusHistory" : [ {
       |        "timestamp" : "2020-01-02T03:04:05.006Z",
       |        "completed" : false,
       |        "Submission.StatusType" : "answering"
       |      }, {
       |        "timestamp" : "2020-01-02T03:04:05.006Z",
       |        "requestedBy" : "bob@example.com",
       |        "Submission.StatusType" : "created"
       |      } ]
       |    } ],
       |    "context" : {
       |      "IN_HOUSE_SOFTWARE" : "No",
       |      "VAT_OR_ITSA" : "No",
       |      "NEW_TERMS_OF_USE_UPLIFT" : "No"
       |    }
       |  },
       |  "questionnaireProgress" : {
       |    "796336a5-f7b4-4dad-8003-a818e342cbb4" : {
       |      "state" : "NotStarted",
       |      "questionsToAsk" : [ "653d2ee4-09cf-46a0-bc73-350a385ae860", "6139f57d-36ab-4338-85b3-a2079d6cf376", "3c5cd29d-bec2-463f-8593-cd5412fab1e5" ]
       |    },
       |    "ac69b129-524a-4d10-89a5-7bfa46ed95c7" : {
       |      "state" : "InProgress",
       |      "questionsToAsk" : [ "b9dbf0a5-e72b-4c89-a735-26f0858ca6cc", "a12f314e-bc12-4e0d-87ba-1326acb31008" ]
       |    },
       |    "3a7f3369-8e28-447c-bd47-efbabeb6d93f" : {
       |      "state" : "Completed",
       |      "questionsToAsk" : [ ]
       |    },
       |    "1e4a1369-8e28-447c-bd47-efbabec1a43b" : {
       |      "state" : "$questionnaireState",
       |      "questionsToAsk" : [ ]
       |    }
       |  }
       |}""".stripMargin
  }

  "submission questionIdsOfInterest app name" in {
    Submission.updateLatestAnswersTo(samplePassAnswersToQuestions)(aSubmission).latestInstance.answersToQuestions(
      aSubmission.questionIdsOfInterest.applicationNameId
    ) shouldBe TextAnswer("name of software")
  }

  "submission instance state history" in {
    aSubmission.latestInstance.statusHistory.head.isOpenToAnswers shouldBe true
    aSubmission.latestInstance.isOpenToAnswers shouldBe true
    aSubmission.status.isOpenToAnswers shouldBe true
  }

  "submission instance is in progress" in {
    aSubmission.latestInstance.isOpenToAnswers shouldBe true
  }

  "submission is in progress" in {
    aSubmission.status.isOpenToAnswers shouldBe true
  }

  "submission findQuestionnaireContaining" in {
    aSubmission.findQuestionnaireContaining(aSubmission.questionIdsOfInterest.applicationNameId) shouldBe Some(CustomersAuthorisingYourSoftware.questionnaire)
  }

  "submission setLatestAnswers" in {
    val newAnswersToQuestions = Map(
      (OrganisationDetails.question1.id -> TextAnswer("new web site"))
    )

    Submission.updateLatestAnswersTo(newAnswersToQuestions)(aSubmission).latestInstance.answersToQuestions(OrganisationDetails.question1.id) shouldBe TextAnswer("new web site")
  }

  "submission automaticallyMark pass" in {
    val answering1       = Submission.addStatusHistory(Submission.Status.Answering(instant, false))(aSubmission)
    val answering2       = Submission.updateLatestAnswersTo(samplePassAnswersToQuestions)(answering1)
    val answered         = Submission.addStatusHistory(Submission.Status.Answering(instant, true))(answering2)
    val submitted        = Submission.submit(instant, "bob@example.com")(answered)
    val markedSubmission = Submission.automaticallyMark(instant, "bob@example.com")(submitted)
    markedSubmission.latestInstance.isGranted shouldBe true
    markedSubmission.latestInstance.status shouldBe Submission.Status.Granted(instant, "bob@example.com", Some("Automatically passed"), None)
  }

  "submission automaticallyMark fail" in {
    val answering1       = Submission.addStatusHistory(Submission.Status.Answering(instant, false))(aSubmission)
    val answering2       = Submission.updateLatestAnswersTo(sampleFailAnswersToQuestions)(answering1)
    val answered         = Submission.addStatusHistory(Submission.Status.Answering(instant, true))(answering2)
    val submitted        = Submission.submit(instant, "bob@example.com")(answered)
    val markedSubmission = Submission.automaticallyMark(instant, "bob@example.com")(submitted)
    markedSubmission.latestInstance.isFailed shouldBe true
    markedSubmission.latestInstance.status shouldBe Submission.Status.Failed(instant, "bob@example.com")
  }

  "submission automaticallyMark warning" in {
    val answering1       = Submission.addStatusHistory(Submission.Status.Answering(instant, false))(aSubmission)
    val answering2       = Submission.updateLatestAnswersTo(sampleWarningsAnswersToQuestions)(answering1)
    val answered         = Submission.addStatusHistory(Submission.Status.Answering(instant, true))(answering2)
    val submitted        = Submission.submit(instant, "bob@example.com")(answered)
    val markedSubmission = Submission.automaticallyMark(instant, "bob@example.com")(submitted)
    markedSubmission.latestInstance.isWarnings shouldBe true
    markedSubmission.latestInstance.status shouldBe Submission.Status.Warnings(instant, "bob@example.com")
  }

  "questionnaire state description" in {
    QuestionnaireState.describe(QuestionnaireState.NotStarted) shouldBe "Not Started"
    QuestionnaireState.describe(QuestionnaireState.InProgress) shouldBe "In Progress"
    QuestionnaireState.describe(QuestionnaireState.NotApplicable) shouldBe "Not Applicable"
    QuestionnaireState.describe(QuestionnaireState.Completed) shouldBe "Completed"
  }

  "questionnaire state is completed" in {
    QuestionnaireState.isCompleted(QuestionnaireState.NotStarted) shouldBe false
    QuestionnaireState.isCompleted(QuestionnaireState.InProgress) shouldBe false
    QuestionnaireState.isCompleted(QuestionnaireState.Completed) shouldBe true
  }

  "shouldAsk" in {
    AskWhen.shouldAsk(standardContext, answersToQuestions)(OrganisationDetails.questionnaire.questions.head.askWhen) shouldBe true
    AskWhen.shouldAsk(standardContext, answersToQuestions)(OrganisationDetails.questionnaire.questions.tail.head.askWhen) shouldBe true
    AskWhen.shouldAsk(standardContext, answersToQuestions)(CustomersAuthorisingYourSoftware.questionnaire.questions.tail.tail.head.askWhen) shouldBe true
  }

  "submission status isOpenToAnswers" in {
    Submission.Status.Answering(instant, false).isOpenToAnswers shouldBe true
  }

  "submission status isCreated" in {
    Submission.Status.Answering(instant, false).isCreated shouldBe false
  }

  "submission status isGrantedWithOrWithoutWarnings" in {
    Submission.Status.Answering(instant, false).isGrantedWithOrWithoutWarnings shouldBe false
    Submission.Status.Granted(instant, "bob@example.com", None, None).isGrantedWithOrWithoutWarnings shouldBe true
    Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None).isGrantedWithOrWithoutWarnings shouldBe true
  }

  "submission status isGrantedWithWarnings" in {
    Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None).isGrantedWithWarnings shouldBe true
  }

  "submission status isDeclined" in {
    Submission.Status.Declined(instant, "bob@example.com", "reasons").isDeclined shouldBe true
  }

  "submission status isPendingResponsibleIndividual" in {
    Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com").isPendingResponsibleIndividual shouldBe true
  }

  "submission status isLegalTransition" in {
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Failed(instant, "bob@example.com")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Warnings(instant, "bob@example.com")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Granted(instant, "bob@example.com", Some("comments"), None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.PendingResponsibleIndividual(instant, "bob@example.com"),
      Submission.Status.Declined(instant, "bob@example.com", "reasons")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Failed(instant, "bob@example.com"),
      Submission.Status.Granted(instant, "bob@example.com", Some("comments"), None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Failed(instant, "bob@example.com"),
      Submission.Status.Declined(instant, "bob@example.com", "reasons")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Warnings(instant, "bob@example.com"),
      Submission.Status.Granted(instant, "bob@example.com", Some("comments"), None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Warnings(instant, "bob@example.com"),
      Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None),
      Submission.Status.Declined(instant, "bob@example.com", "reasons")
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.GrantedWithWarnings(instant, "bob@example.com", "warnings", None),
      Submission.Status.Granted(instant, "bob@example.com", None, None)
    ) shouldBe true
    Submission.Status.isLegalTransition(
      Submission.Status.Answering(instant, false),
      Submission.Status.Granted(instant, "bob@example.com", None, None)
    ) shouldBe false
  }

  "toJson for extended submission" in {
    Json.prettyPrint(Json.toJson(partiallyAnsweredExtendedSubmission)) shouldBe jsonSubmission()
  }

  "read extended submssion from json" in {
    testFromJson[ExtendedSubmission](jsonSubmission())(partiallyAnsweredExtendedSubmission)
  }

  "read invalid extended submission from json" in {
    intercept[Exception] {
      testFromJson[ExtendedSubmission](jsonSubmission("invalid-questionnaire-state"))(partiallyAnsweredExtendedSubmission)
    }
  }
}
