package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


case class CreateAccountToListParams(locationId: String, userId: String)
case class DeleteAccountToListParams(locationId: String, userId: String)


object GroceryUserToListRoutes extends Logging {

  implicit val createAccountToListJsonMarshal = jsonFormat2(CreateAccountToListParams)
  implicit val deleteAccountToListJsonMarshal = jsonFormat2(DeleteAccountToListParams)


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
    var items: List[Map[String, String]] = List[Map[String, String]]()
    while (rs.next()) {
      items = items :+ Map(
        "user_id" -> rs.getString("user_id"),
        "list_id" -> rs.getString("list_id")
      )
    }
    rs.close()
    items
  }


  def CreateAccount(): Route = path(CreateAccountPath) {
    pathEndOrSingleSlash {
      put {
        entity(as[CreateAccountToListParams]) { params =>
          complete {
            val query = SqLQueries.InsertQuery(TableUserListName, Map("list_id" -> params.locationId, "user_id" -> params.userId))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
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
          if (mappedUsertoList.nonEmpty) HttpResponse(entity = mappedUsertoList.toJson.toString)
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteAccountById(): Route = path(DeleteAccountPath) {
    pathEndOrSingleSlash {
      delete {
        entity(as[DeleteAccountToListParams]) { params =>
          complete {
            val query = SqLQueries.DeleteQuery(TableUserListName, Map("list_id" -> params.locationId, "user_id" -> params.userId))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
        }
      }
    }
  }
}
