package validation

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.util.query.Query0
import io.circe.Json
import io.circe.parser.parse

import scala.collection.mutable

trait SchemaRepository {

  def addSchema(id: SchemaId, schema: ValidatedSchema): IO[Unit]

  def getSchema(id: SchemaId): IO[Option[ValidatedSchema]]
}

object InMemorySchemaRepository extends SchemaRepository {

  private val schemaMap = mutable.HashMap.empty[String, ValidatedSchema]

  def addSchema(id: SchemaId, schema: ValidatedSchema): IO[Unit] =
    IO.pure(schemaMap.update(id.id, schema))

  def getSchema(id: SchemaId): IO[Option[ValidatedSchema]] =
    IO.pure(schemaMap.get(id.id))

}

object SqliteSchemaRepository extends SchemaRepository {

  val xa = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", "jdbc:sqlite:schemas.sqlite")
  Statements.ddl.run.transact(xa).unsafeRunSync() // we want it to crash early, hence unsafeRunSync

  //implicit val doobieLogging: LogHandler = LogHandler.jdkLogHandler

  object Statements {
    val ddl: Update0 =
      sql"""
        CREATE TABLE IF NOT EXISTS schemas (
          schema_id TEXT PRIMARY KEY NOT NULL,
          schema_json TEXT NOT NULL
        );""".update

    def getSchema(id: String): Query0[String] =
      sql"SELECT schema_json FROM schemas where schema_id = $id"
        .query[String]

    def addSchema(id: String, schema: String): Update0 =
      sql"INSERT OR REPLACE INTO schemas (schema_id, schema_json) VALUES ($id, $schema)"
        .update
  }

  def addSchema(id: SchemaId, schema: ValidatedSchema): IO[Unit] =
    Statements.addSchema(id.id, schema.json.toString)
      .run
      .transact(xa)
      .map(_ => ())

  def getSchema(id: SchemaId): IO[Option[ValidatedSchema]] =
    Statements.getSchema(id.id)
      .map(str => ValidatedSchema(parse(str).right.get))
      .option
      .transact(xa)
}
