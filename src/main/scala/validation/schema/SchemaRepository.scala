package validation.schema

import cats.effect.IO

import scala.collection.mutable

// In-memory for now
object SchemaRepository {

  private val schemaMap = mutable.HashMap.empty[String, ValidatedSchema]

  def addSchema(id: SchemaId, schema: ValidatedSchema): IO[Unit] =
    IO.pure(schemaMap.update(id.id, schema))

  def getSchema(id: SchemaId): IO[Option[ValidatedSchema]] =
    IO.pure(schemaMap.get(id.id))

}
