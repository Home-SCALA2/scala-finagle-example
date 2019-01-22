package me.eax.finagle_example.tests

import com.escalatesoft.subcut.inject.NewBindingModule._
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import me.eax.finagle_example.dao.{FinagleExampleStorage, FinagleExampleStorageImpl}
import me.eax.finagle_example.services.FinagleExampleService
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil
import org.scalatest._

class RestSpec extends FunSpec with Matchers {
  implicit val bindings = newBindingModule { module =>
    import module._

    bind[FinagleExampleStorage] toSingle new FinagleExampleStorageImpl
  }

  val server = Http.serve(":8090", new FinagleExampleService)
  val service: Service[HttpRequest, HttpResponse] = Http.newService("localhost:8090")

  describe("Server") {
    it("returns 'not found' for non-exiting key") {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some-key")
      val response = Await.result(service(request))
      response.getStatus shouldBe HttpResponseStatus.NOT_FOUND
    }

    it("saves, returns and deletes values") {
      // save
      {
        val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/some-key")
        request.setContent(ChannelBuffers.copiedBuffer("some-value", CharsetUtil.UTF_8))
        val response = Await.result(service(request))
        response.getStatus shouldBe HttpResponseStatus.OK
      }
      // load
      {
        val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some-key")
        val response = Await.result(service(request))
        response.getStatus shouldBe HttpResponseStatus.OK
        response.getContent.toString(CharsetUtil.UTF_8) shouldBe "some-value"
      }
      // delete
      {
        val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, "/some-key")
        val response = Await.result(service(request))
        response.getStatus shouldBe HttpResponseStatus.OK
      }
      // check
      {
        val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some-key")
        val response = Await.result(service(request))
        response.getStatus shouldBe HttpResponseStatus.NOT_FOUND
      }
    }

    it("returns 'bad request' for bad requests") {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/some-key")
      val response = Await.result(service(request))
      response.getStatus shouldBe HttpResponseStatus.BAD_REQUEST
    }
  }
}
