package controllers.v1

import java.sql.Date
import java.text.SimpleDateFormat

import com.google.inject._

import scala.util._
import scala.concurrent._
import akka.actor.ActorSystem
import org.birdfeed.chirp.database.{Query, Tables}
import org.birdfeed.chirp.database.models.Experiment
import org.birdfeed.chirp.actions.EndpointHandler
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.DatabaseConfigProvider
import play.libs.Json
import slick.driver.JdbcProfile


@Singleton
class ExperimentController @Inject() (actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with EndpointHandler with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create = Action.async(BodyParsers.parse.json) { request =>
    val createReads: Reads[Future[Try[Experiment]]] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "start_date").read[String] and
        (JsPath \ "end_date").readNullable[String]
      ) ((name: String, startDate: String, endDate: Option[String]) => {
      val format = new SimpleDateFormat("MMddYYYY")
      val sqlStartDate = new java.sql.Date(format.parse(startDate).getTime)
      Experiment.create(name, sqlStartDate, endDate.map { date =>
        new java.sql.Date(format.parse(date).getTime)
      })
    })

    dtoWithMarshallingSingle(createReads, request.body, Created)
  }

  def retrieve(id: String) = Action.async { request =>
    dtoWithErrorHandlingSingle(Experiment.find(id.toInt), Ok)
  }

  def delete(id: String) = Action.async { request =>
    anyWithErrorHandlingSingle(Experiment.delete(id.toInt), Ok)
  }

  /*def update(id: String) = Action.async(BodyParsers.parse.json) { request =>
    val updateReads: Reads[Future[Try[Experiment]]] = (
      (JsPath \ "name").readNullable[String] and
        (JsPath \ "startDate").readNullable[String] and
        (JsPath \ "endDate").readNullable[String]
      ) ((expName: Option[String], startDate: Option[String], endDate: Option[String]) => {
      Experiment.find(id.toInt).flatMap {
        case Success(retrieved) => {
          val format = new SimpleDateFormat("MMddYYYY")
          val sqlStartDate = new java.sql.Date(format.parse(startDate.get).getTime)
          val (updatedName, updatedStartDate, updatedEndDate) = (
            expName.getOrElse(retrieved.name),
            if (startDate != null) new java.sql.Date(format.parse(startDate.get).getTime) else retrieved.startDate,
            if (endDate != null) new java.sql.Date(format.parse(endDate.get).getTime) else retrieved.endDate
            )

          val updatedRow = retrieved.slickTE.copy(
            retrieved.id,
            updatedName,
            updatedStartDate,
            updatedEndDate.asInstanceOf[Option[Date]],
            retrieved.createdAt,
            retrieved.updatedAt
          )

          Future {
            Experiment.updateById(id.toInt, updatedRow)
          }
        }
      }
    })

    dtoWithMarshallingSingle(updateReads, request.body, Ok)
  }*/
}



