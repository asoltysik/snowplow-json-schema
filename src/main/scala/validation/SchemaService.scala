package validation

import cats.effect.IO
import com.github.fge.jsonschema.core.report.ProcessingMessage
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

class SchemaService(val repository: SchemaRepository) {

  implicit val encodeProcessingMessage: Encoder[ProcessingMessage] =
    Encoder.instance { a: ProcessingMessage =>
      Json.fromString(a.toString)
    }

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
        .map(SchemaValidation.validateSchema(_))
        .flatMap {
          case Right(schema) =>
            repository.addSchema(id, schema)
            Ok(Responses.success("uploadSchema", id).asJson)
          case Left(nel) =>
            Ok(Responses.error("uploadSchema", id, nel.map(_.toString).toList: _*).asJson)
        }
  }

}
