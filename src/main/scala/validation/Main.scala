package validation

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.HttpService
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {

  val schemaService: HttpService[IO] = new SchemaService(SqliteSchemaRepository).service
  val validateService: HttpService[IO] = new ValidationService(SqliteSchemaRepository).service

  override def stream(args: List[String],
                      requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .mountService(schemaService, "/schema")
      .mountService(validateService, "/validate")
      .serve
}
