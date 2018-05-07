package validation

import cats.effect.IO

import scala.collection.mutable

object InMemorySchemaRepository extends SchemaRepository {

  private val schemaMap = mutable.HashMap.empty[String, ValidatedSchema]

  def addSchema(id: SchemaId, schema: ValidatedSchema): IO[Boolean] =
    IO {
      schemaMap.update(id.id, schema)
      true
    }

  def getSchema(id: SchemaId): IO[Option[ValidatedSchema]] =
    IO(schemaMap.get(id.id))
}
