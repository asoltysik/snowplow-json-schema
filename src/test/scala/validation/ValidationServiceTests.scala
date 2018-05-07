package validation

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import utest._

object ValidationServiceTests extends TestSuite{

  implicit val responseDecoder = jsonOf[IO, Responses.Response]

  val service = new ValidationService(InMemorySchemaRepository).service

  val tests = Tests {
    "POST returns successful response for valid json and existent schema" - {
      InMemorySchemaRepository.addSchema(SchemaId("v1"), ValidatedSchema(TestUtils.sampleValidSchema)).unsafeRunSync()

      val request = Request[IO](POST, Uri.uri("/v1")).withBody(TestUtils.sampleValidJson).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val body = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == Ok)
      assert(body == Responses.success("validateDocument", SchemaId("v1")))
    }

    "POST returns error response with messages for invalid json and existent schema" - {
      InMemorySchemaRepository.addSchema(SchemaId("v2"), ValidatedSchema(TestUtils.sampleValidSchema)).unsafeRunSync()

      val request = Request[IO](POST, Uri.uri("/v2")).withBody(TestUtils.sampleInvalidJson).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val body = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == BadRequest)
      assertMatch(body) {
        case Responses.Response("validateDocument", SchemaId("v2"), "error", head :: tail) =>
      }
    }

    "POST returns 404 for nonexistent schema" - {
      val request = Request[IO](POST, Uri.uri("/v3")).withBody(TestUtils.sampleValidJson).unsafeRunSync()
      val response = service(request).value.unsafeRunSync()

      assert(response.isDefined)
      assert(response.get.status == NotFound)
    }

    "POST with config schema + nulls returns successful response" - {
      InMemorySchemaRepository.addSchema(SchemaId("v4"), ValidatedSchema(TestUtils.configSchema)).unsafeRunSync()

      val request = Request[IO](POST, Uri.uri("/v4")).withBody(TestUtils.config).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val body = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == Ok)
      assert(body == Responses.success("validateDocument", SchemaId("v4")))
    }
  }
}
