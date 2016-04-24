package Routes

import SQL.SqLQueries
import Util.{CorsHandler, Logging}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials

import scala.concurrent.Future


trait MainRoute extends Logging with CorsHandler {

  val MainPath = "groceries"
  val AppPath = "app"

  val CreateAccount = "new_account"

  def routes: Route = {

    corsHandler {
      pathPrefix(MainPath) {
        {
          authenticateBasic(realm = "secure_api", authenticateUser) { user =>
            paths
          } ~ unauthorizedPath
        }
      }
    } ~
      errorPath
  }

  def authenticateUser(credentials: Credentials): Option[String] = {
    credentials match {
      case p: Credentials.Provided =>
        logger.debug("USER:" + p.identifier)
        val userInfo = SqLQueries.SelectQuery("user", List[String]("pass"), ("email", p.identifier))
        userInfo.next()
        if (userInfo.first && p.verify(userInfo.getString("pass"))) {
          userInfo.close(); Some(p.identifier)
        }
        else None
      case _ => None
    }
  }

  val paths: Route = {
    pathPrefix(AppPath) {
      GroceryListRoutes.routes ~
        GroceryItemsRoutes.routes ~
        GroceryAccountsRoutes.routes ~
        GroceryListUsersRoutes.routes
    }
  }

  val errorPath: Route = path("error") {
    get {
      complete {
        HttpResponse(StatusCodes.InternalServerError, entity = "Something went bad")
      }
    }
  }

  val unauthorizedPath: Route = path("unauthorized") {
    get {
      complete {
        HttpResponse(StatusCodes.Unauthorized, entity = "You shall not pass.")
      }
    }
  }
}

