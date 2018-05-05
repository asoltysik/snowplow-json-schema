package validation

import cats.effect.IO
import io.circe.Json
import io.circe.literal._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import utest._

object SchemaServiceTests extends TestSuite {

  implicit val responseDecoder = jsonOf[IO, Responses.Response]

  val service = new SchemaService(InMemorySchemaRepository).service

  val sampleValidSchema = json"""{"type": "number"}"""
  val sampleInvalidSchema = json"""{"type": "cats.effect.IO"}"""
  val sampleValidJson = json"""13"""
  val sampleInvalidJson = json"""{"foo": "bar"}"""

  val tests = Tests {
    "GET returns 404 for nonexistent schema id" - {
      val request = Request[IO](GET, Uri.uri("/1"))
      val responseOpt = service(request).value.unsafeRunSync()

      assert(responseOpt.isDefined)
      responseOpt.foreach { response =>
        assert(response.status == NotFound)
      }
    }

    "GET returns json for existent schema id" - {
      InMemorySchemaRepository.addSchema(SchemaId("2"), ValidatedSchema(sampleValidSchema))
      val request = Request[IO](GET, Uri.uri("/2"))
      val response = service.orNotFound.run(request).unsafeRunSync()

      assert(response.as[Json].unsafeRunSync() == sampleValidSchema)
      assert(response.status == Ok)
    }

    "POST returns successful response for valid schema" - {
      val request = Request[IO](POST, Uri.uri("/3")).withBody(sampleValidSchema).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val responseBody = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == Created)
      assert(responseBody == Responses.success("uploadSchema", SchemaId("3")))
    }

    "POST returns error response for invalid schema" - {
      val request = Request[IO](POST, Uri.uri("/4")).withBody(sampleInvalidSchema).unsafeRunSync()
      val response = service.orNotFound.run(request).unsafeRunSync()
      val responseBody = response.as[Responses.Response].unsafeRunSync()

      assert(response.status == BadRequest)
      assertMatch(responseBody) {
        case Responses.Response("uploadSchema", SchemaId("4"), "error", _) =>
      }
      assert(responseBody.messages.nonEmpty)
    }
  }

}
