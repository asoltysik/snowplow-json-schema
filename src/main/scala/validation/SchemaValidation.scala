package validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import io.circe.Json

object SchemaValidation {

  private val schemaFactory = JsonSchemaFactory.byDefault
  private val validator = schemaFactory.getSyntaxValidator

  def validateSchema(schema: Json): (ProcessingReport, ValidatedSchema) = {
    val report = validator.validateSchema(JsonLoader.fromString(schema.toString))
    (report, ValidatedSchema(schema))
  }

  def validateJson(validSchema: ValidatedSchema, jsonToValidate: Json): ProcessingReport = {
    val schema = schemaFactory.getJsonSchema(JsonLoader.fromString(validSchema.json.toString))
    schema.validate(JsonLoader.fromString(jsonToValidate.toString))
  }

}
