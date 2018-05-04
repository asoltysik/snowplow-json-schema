package validation

import io.circe.Json
import io.circe.generic.JsonCodec

package object schema {

  @JsonCodec case class SchemaId(id: String) extends AnyVal

  @JsonCodec case class ValidatedSchema(json: Json) extends AnyVal

}
