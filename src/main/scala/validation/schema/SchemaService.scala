package validation.schema

import cats.effect.IO
import com.github.fge.jsonschema.core.report.ProcessingMessage
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.syntax._
import io.circe.generic.extras.encoding.UnwrappedEncoder.encodeUnwrapped
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

object Responses {
  @JsonCodec case class Response(action: String,
                                 id: SchemaId,
                                 status: String,
                                 messages: List[String])

  def success(action: String, id: SchemaId, messages: String*): Response =
    Response(action, id, "success", messages.toList)

  def error(action: String, id: SchemaId, messages: String*): Response =
    Response(action, id, "error", messages.toList)

}

object SchemaService {

  implicit val encodeProcessingMessage: Encoder[ProcessingMessage] =
    Encoder.instance { a: ProcessingMessage =>
      Json.fromString(a.toString)
    }

  val service = HttpService[IO] {
    case GET -> Root / schemaId =>
      SchemaRepository
        .getSchema(SchemaId(schemaId))
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
            SchemaRepository.addSchema(id, schema)
            Ok(Responses.success("uploadSchema", id).asJson)
          case Left(nel) =>
            Ok(
              Responses
                .error("uploadSchema", id, nel.map(_.toString).toList: _*)
                .asJson)
        }
  }

}
