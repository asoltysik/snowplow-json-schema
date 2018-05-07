package validation

import cats.effect.IO
import io.circe.{Json, Printer}
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

import scala.collection.JavaConverters._

class ValidationService(val repository: SchemaRepository) {

  private def removeNulls(json: Json): Json = {
    if(json.isObject) {
      json.mapObject(_.filter {
        case (_, Json.Null) => false
        case _ => true
      }.mapValues(removeNulls))
    } else {
      json
    }
  }

  val service = HttpService[IO] {
    case req @ POST -> Root / schemaId =>
      val id = SchemaId(schemaId)
      repository.getSchema(id).flatMap {
        case Some(schema) =>
          req.as[Json]
            .map(removeNulls)
            .map(SchemaValidation.validateJson(schema, _))
            .flatMap { report =>
              if(report.isSuccess) {
                Ok(Responses.success("validateDocument", id).asJson)
              }
              else {
                val errors = report.iterator.asScala.toList.map(_.toString)
                BadRequest(Responses.error("validateDocument", id, errors: _*).asJson)
              }
            }

        case None =>
          NotFound()
      }
  }
}
