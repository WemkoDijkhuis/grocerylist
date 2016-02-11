package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json._
import spray.json.DefaultJsonProtocol._

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

  def mapAccounts(rs: ResultSet): List[Map[String, String]] = {
    var items: List[Map[String, String]] = List[Map[String,String]]()
    while (rs.next()) {
      items = items :+ Map(
        "user_id" -> rs.getString("user_id"),
        "list_id" -> rs.getString("list_id")
      )
    }
    rs.close()
    items
  }

  def CreateAccount(): Route = path(CreateAccountPath / Segments(2)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query =SqLQueries.InsertQuery(TableUserListName, Map("list_id" -> itemvalues(1), "user_id" -> itemvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def GetAccountById(): Route = path(GetUsersFromList / Segment) { listid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.SelectQuery(TableUserListName, List[String]("user_id"), ("list_id", listid))
          val mappedUsertoList = mapAccounts(query)
          if(mappedUsertoList.nonEmpty) HttpResponse(entity = mappedUsertoList.toJson.toString)
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteAccountById(): Route = path(DeleteAccountPath / Segments(2)) { userlistvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.DeleteQuery(TableUserListName, Map("list_id" -> userlistvalues(1), "user_id" -> userlistvalues(0) ))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }
}
