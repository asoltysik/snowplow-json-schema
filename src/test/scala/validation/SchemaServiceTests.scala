package validation

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import utest._

object SchemaServiceTests extends TestSuite {

  implicit val responseDecoder = jsonOf[IO, Responses.Response]

  val service = new SchemaService(InMemorySchemaRepository).service

  val tests = Tests {
    "GET returns 404 for nonexistent schema id" - {
      val request = Request[IO](GET, Uri.uri("/s1"))
      val responseOpt = service(request).value.unsafeRunSync()

      assert(responseOpt.isDefined)
      responseOpt.foreach { response =>
        assert(response.status == NotFound)
      }
    }

    "GET returns json for existent schema id" - {
      InMemorySchemaRepository.addSchema(SchemaId("s2"), ValidatedSchema(TestUtils.sampleValidSchema)).unsafeRunSync()
      val request = Request[IO](GET, Uri.uri("/s2"))
      val response = service.orNotFound.run(request).unsafeRunSync()

      assert(response.status == Ok)
      assert(response.as[Json].unsafeRunSync() == TestUtils.sampleValidSchema)
    }

    "POST returns successful response for valid schema" - {
      val request = Request[IO](POST, Uri.uri("/s3")).withBody(TestUtils.sampleValidSchema).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val responseBody = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == Created)
      assert(responseBody == Responses.success("uploadSchema", SchemaId("s3")))
    }

    "POST returns error response for invalid schema" - {
      val request = Request[IO](POST, Uri.uri("/s4")).withBody(TestUtils.sampleInvalidSchema).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val responseBody = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == BadRequest)
      assertMatch(responseBody) {
        case Responses.Response("uploadSchema", SchemaId("s4"), "error", head :: tail) =>
      }
    }
  }

}
