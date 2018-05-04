package validation.schema

import cats.data.NonEmptyList
import cats.syntax.list._
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import io.circe.Json

import scala.collection.JavaConverters._

object SchemaValidation {

  private val validator = JsonSchemaFactory.byDefault.getSyntaxValidator

  def validateSchema(schema: Json): Either[NonEmptyList[ProcessingMessage], ValidatedSchema] = {
    val report =
      validator.validateSchema(JsonLoader.fromString(schema.toString))

    if (report.isSuccess) {
      Right(ValidatedSchema(schema))
    } else {
      // Using get because if validation fails, there must be at least one processing message
      val processingMessages = report.iterator().asScala.toList.toNel.get
      Left(processingMessages)
    }
  }

}
