package validation

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._

object SchemaService {

  val service = HttpService[IO] {
    case GET -> Root / schemaId =>
      Ok(s"Got GET $schemaId")

    case POST -> Root / schemaId =>
      Ok(s"Got POST $schemaId")
  }

}
