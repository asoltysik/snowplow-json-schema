package validation

import io.circe.literal._

object TestUtils {

  val sampleValidSchema = json"""{"type": "number"}"""
  val sampleInvalidSchema = json"""{"type": "cats.effect.IO"}"""

  val sampleValidJson = json"""13"""
  val sampleInvalidJson = json"""{"foo": "bar"}"""

}
