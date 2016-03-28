package Util

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpHeader, HttpResponse}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.typesafe.config.ConfigFactory

trait CorsHandler {
  lazy val allowedOrigin = {
    val config = ConfigFactory.load()
    val sAllowedOrigin = config.getString("cors.allowed-origin")
    if (sAllowedOrigin == "*")
      `Access-Control-Allow-Origin`.*
    else
      `Access-Control-Allow-Origin`(HttpOrigin(sAllowedOrigin))
  }

  //this rejection handler adds access control headers to Authentication Required response
  implicit val unauthRejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case AuthenticationFailedRejection(_, challenge) =>
        complete(
          HttpResponse(401)
            .withHeaders(allowedOrigin,`Access-Control-Allow-Credentials`(true),`WWW-Authenticate`(challenge))
            .withEntity("Authentication missing")
        )
    }
    .result()

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    mapResponseHeaders { headers =>
      allowedOrigin +:
        `Access-Control-Allow-Credentials`(true) +:
        headers
    }
  }

  //this handles preflight OPTIONS requests. TODO: see if can be done with rejection handler,
  //otherwise has to be under addAccessControlHeaders
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(200).withHeaders(
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE) :: List[HttpHeader]( `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")), `Access-Control-Allow-Origin`(corsAllowedHeaders.mkString(", ")))
    )
    )
  }

  val corsAllowedHeaders: List[String] = List("Authorization")


  def corsHandler(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
