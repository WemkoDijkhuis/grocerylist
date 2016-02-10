package Routes

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object GroceryAccountsRoutes extends Logging {

  val TableAccountsName = "user"

  val GroceryAccountsRouteName = "groceryaccounts"

  val GetAccountPath = "gaccount"
  val CreateAccountPath = "naccount"
  val DeleteAccountPath = "daccount"
  val UpdateAccountPath = "uaccount"

  def routes: Route =
    pathPrefix(GroceryAccountsRouteName) {
      GetAccountById() ~
        CreateAccount() ~
        DeleteAccountById() ~
        UpdateAccountById()
    }

  def CreateAccount(): Route = path(CreateAccountPath / Segments(3)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.InsertQuery(TableAccountsName, Map("name" -> itemvalues(2), "pass" -> itemvalues(1), "email" -> itemvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def GetAccountById(): Route = path(GetAccountPath / Segment) { userid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.SelectQuery(TableAccountsName, List[String]("id", "name", "email"), ("id", userid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def DeleteAccountById(): Route = path(DeleteAccountPath / Segment) { userid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.DeleteQuery(TableAccountsName, Map("id" -> userid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def UpdateAccountById(): Route = path(UpdateAccountPath / Segments(4)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.UpdateQuery(TableAccountsName, Map("name" -> itemvalues(3), "pass" -> itemvalues(2), "email" -> itemvalues(1)), ("id", itemvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }
}
