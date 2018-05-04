package validation.schema

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import utest._

object SchemaServiceTests extends TestSuite {

  val tests = Tests {
    "GET returns 404 for nonexistent schema id" - {
      val request = Request[IO](GET, Uri.uri("/schema/34"))
      val response = SchemaService.service.orNotFound.run(request).unsafeRunSync()

      assert(response.status == NotFound)
    }
  }

}
