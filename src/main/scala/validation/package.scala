import io.circe.{Decoder, Json, ObjectEncoder}
import io.circe.generic.JsonCodec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.literal._
import io.circe.generic.extras.encoding.UnwrappedEncoder.encodeUnwrapped
import io.circe.generic.extras.decoding.UnwrappedDecoder.decodeUnwrapped

package object validation {

  object Responses {

    implicit val circeConfig: Configuration = Configuration.default.withDefaults

    implicit val responseEncoder: ObjectEncoder[Response] = deriveEncoder[Response].mapJsonObject(
      _.filter {
        case ("messages", obj) => obj != json"[]"
        case _ => true
      }
    )

    implicit val responseDecoder: Decoder[Response] = deriveDecoder[Response]

    case class Response(action: String,
                        id: SchemaId,
                        status: String,
                        messages: List[String] = List())

    def success(action: String, id: SchemaId, messages: String*): Response =
      Response(action, id, "success", messages.toList)

    def error(action: String, id: SchemaId, messages: String*): Response =
      Response(action, id, "error", messages.toList)

  }

  @JsonCodec case class SchemaId(id: String) extends AnyVal

  @JsonCodec case class ValidatedSchema(json: Json) extends AnyVal

}
