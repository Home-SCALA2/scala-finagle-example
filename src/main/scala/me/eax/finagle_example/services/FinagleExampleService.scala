package me.eax.finagle_example.services

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.twitter.finagle.Service
import com.twitter.finagle.http.Response
import com.twitter.util.{Future => TwitterFuture}
import com.typesafe.scalalogging._
import me.eax.finagle_example.dao.FinagleExampleStorage
import me.eax.finagle_example.utils._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FinagleExampleService(implicit val bindingModule: BindingModule) extends Service[HttpRequest, HttpResponse] with Injectable {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  private val dao = inject[FinagleExampleStorage]

  logger.info(s"service started")

  def apply(req: HttpRequest): TwitterFuture[HttpResponse] =
  {
    val resp = Response(req.getProtocolVersion, HttpResponseStatus.OK)
    val someUri = req.getUri

    req.getMethod match {
      case HttpMethod.GET =>
        logger.debug(s"reading $someUri")
        for {
          optValue <- dao.get(someUri)
        } yield {
          optValue match {
            case None =>
              resp.setStatus(HttpResponseStatus.NOT_FOUND)
            case Some(value) =>
              resp.setContentString(value)
          }
          resp
        }
      case HttpMethod.POST =>
        logger.debug(s"writing $someUri")
        val value = req.getContent.toString(CharsetUtil.UTF_8)
        dao.update(someUri, value).map(_ => resp)
      case HttpMethod.DELETE =>
        logger.debug(s"deleting $someUri")
        dao.remove(someUri).map(_ => resp)
      case _ =>
        logger.error(s"bad request: $req")
        resp.setStatus(HttpResponseStatus.BAD_REQUEST)
        Future.successful(resp)
    }
  }
}
