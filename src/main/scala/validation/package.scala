import io.circe.Json
import io.circe.generic.JsonCodec
import io.circe.generic.extras.encoding.UnwrappedEncoder.encodeUnwrapped
import io.circe.generic.extras.decoding.UnwrappedDecoder.decodeUnwrapped

package object validation {

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


  @JsonCodec case class SchemaId(id: String) extends AnyVal

  @JsonCodec case class ValidatedSchema(json: Json) extends AnyVal
}
