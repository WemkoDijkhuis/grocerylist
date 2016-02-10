package Routes

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object GroceryUserToListRoutes extends Logging {

  val TableUserListName = "list_users"

  val GroceryAccountsRouteName = "groceryusertolist"

  val GetUsersFromList = "guseraccount"
  val CreateAccountPath = "nuseraccount"
  val DeleteAccountPath = "duseraccount"

  def routes: Route =
    pathPrefix(GroceryAccountsRouteName) {
      GetAccountById() ~
        CreateAccount() ~
        DeleteAccountById()
    }

  def CreateAccount(): Route = path(CreateAccountPath / Segments(2)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.InsertQuery(TableUserListName, Map("list_id" -> itemvalues(1), "user_id" -> itemvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def GetAccountById(): Route = path(GetUsersFromList / Segment) { listid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.SelectQuery(TableUserListName, List[String]("user_id"), ("list_id", listid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def DeleteAccountById(): Route = path(DeleteAccountPath / Segments(2)) { userlistvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.DeleteQuery(TableUserListName, Map("list_id" -> userlistvalues(1), "user_id" -> userlistvalues(0) ))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }
}
