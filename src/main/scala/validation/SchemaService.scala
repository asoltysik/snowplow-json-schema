package validation

import cats.effect.IO
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

import scala.collection.JavaConverters.asScalaIteratorConverter

class SchemaService(val repository: SchemaRepository) {

  val service = HttpService[IO] {
    case GET -> Root / schemaId =>
      repository.getSchema(SchemaId(schemaId))
        .flatMap {
          case Some(schema) => Ok(schema.json)
          case None => NotFound()
        }

    case req @ POST -> Root / schemaId =>
      val id = SchemaId(schemaId)
      req.as[Json]
        .map(SchemaValidation.validateSchema)
        .flatMap {
          case (report, schema) =>
            if(report.isSuccess) {
              repository.addSchema(id, schema).flatMap { added =>
                if(added)
                  Created(Responses.success("uploadSchema", id).asJson)
                else
                  InternalServerError()
              }
            } else {
              val errors = report.iterator.asScala.toList.map(_.toString)
              BadRequest(Responses.error("uploadSchema", id, errors: _*).asJson)
            }
        }
  }

}
