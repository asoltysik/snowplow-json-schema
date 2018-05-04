package validation

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze._
import validation.schema.SchemaService

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {

  override def stream(args: List[String],
                      requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .mountService(SchemaService.service, "/schema")
      .serve
}
