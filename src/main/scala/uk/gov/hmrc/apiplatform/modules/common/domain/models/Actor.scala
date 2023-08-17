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

package uk.gov.hmrc.apiplatform.modules.common.domain.models

import play.api.libs.json.{Format, JsError, JsObject, JsResult, JsString, JsSuccess, JsValue, Json}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Actors.{AppCollaborator, GatekeeperUser, ScheduledJob}

/** Actor refers to actors that triggered an event
  */
sealed trait Actor

object Actors {

  /** A third party developer who is a collaborator on the application for the event this actor is responsible for triggering
    *
    * @param email
    *   the developers email address at the time of the event
    */
  case class AppCollaborator(email: LaxEmailAddress) extends Actor

  /** A gatekeeper stride user (typically SDST)
    *
    * @param user
    *   the stride user fullname of the gatekeeper user who triggered the event on which they are the actor
    */
  case class GatekeeperUser(user: String) extends Actor

  /** An automated job
    *
    * @param jobId
    *   the job name or instance of the job possibly as a UUID
    */
  case class ScheduledJob(jobId: String) extends Actor

  /** Unknown source - probably 3rd party code such as PPNS invocations
    */
  case object Unknown extends Actor

}

sealed abstract class ActorType(val description: String)

object ActorType {

  implicit val formatActorType = new Format[ActorType] {

    override def writes(o: ActorType): JsValue =
      Json.obj("description" -> o.description, "type" -> o.toString)

    override def reads(json: JsValue): JsResult[ActorType] = {
      (json match {
        case JsString(text) => ActorTypes.fromString(text)
        case JsObject(obj)  => obj.get("type").flatMap(_ match {
            case JsString(t) => ActorTypes.fromString(t)
            case _           => None
          })
        case _              => None
      }).fold[JsResult[ActorType]](JsError(s"Cannot find actor Type"))(JsSuccess(_))
    }
  }
}

object ActorTypes {
  case object COLLABORATOR extends ActorType("Application Collaborator")

  case object GATEKEEPER extends ActorType("Gatekeeper User")

  case object SCHEDULED_JOB extends ActorType("Scheduled Job")

  case object UNKNOWN extends ActorType("Unknown")
  private val ALL = Set(COLLABORATOR, GATEKEEPER, SCHEDULED_JOB, UNKNOWN)

  /*
   * Used for display purposes
   */
  private val lookupDescription: Map[String, ActorType] = ALL.map(at => at.description -> at).toMap

  def fromDescription(text: String): Option[ActorType] = lookupDescription.get(text)

  /*
   * Used for Json only
   */
  private val lookupName: Map[String, ActorType] = ALL.map(at => at.toString -> at).toMap

  def fromString(text: String): Option[ActorType] = lookupName.get(text)

  def actorType(a: Actor): ActorType = a match {
    case _: GatekeeperUser  => GATEKEEPER
    case _: AppCollaborator => COLLABORATOR
    case _: ScheduledJob    => SCHEDULED_JOB
    case _                  => UNKNOWN
  }

}

object Actor {
  import play.api.libs.json._
  import uk.gov.hmrc.play.json.Union

  implicit val actorsCollaboratorWrites: OWrites[Actors.AppCollaborator]  = Json.writes[Actors.AppCollaborator]
  implicit val actorsGatekeeperUserWrites: OWrites[Actors.GatekeeperUser] = Json.writes[Actors.GatekeeperUser]
  implicit val actorsScheduledJobWrites: OWrites[Actors.ScheduledJob]     = Json.writes[Actors.ScheduledJob]

  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._

  implicit val actorsCollaboratorReads: Reads[Actors.AppCollaborator]  =
    ((JsPath \ "id").read[String] or (JsPath \ "email").read[String]).map(s => Actors.AppCollaborator(LaxEmailAddress(s)))
  implicit val actorsGatekeeperUserReads: Reads[Actors.GatekeeperUser] = ((JsPath \ "id").read[String] or (JsPath \ "user").read[String]).map(Actors.GatekeeperUser(_))
  implicit val actorsScheduledJobReads: Reads[Actors.ScheduledJob]     = ((JsPath \ "id").read[String] or (JsPath \ "jobId").read[String]).map(Actors.ScheduledJob(_))

  implicit val actorsUnknownJF: OFormat[Actors.Unknown.type] = Json.format[Actors.Unknown.type]

  implicit val formatNewStyleActor: OFormat[Actor] = Union.from[Actor]("actorType")
    .and[Actors.AppCollaborator](ActorTypes.COLLABORATOR.toString)
    .and[Actors.GatekeeperUser](ActorTypes.GATEKEEPER.toString)
    .and[Actors.ScheduledJob](ActorTypes.SCHEDULED_JOB.toString)
    .and[Actors.Unknown.type](ActorTypes.UNKNOWN.toString)
    .format
}
