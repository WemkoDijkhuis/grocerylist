package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json._

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

  def mapAccounts(rs: ResultSet): List[Map[String, String]] = {
    var items: List[Map[String, String]] = List[Map[String,String]]()
    while (rs.next()) {
      items = items :+ Map(
        "id" -> rs.getString("id"),
        "name" -> rs.getString("name"),
        "email" -> rs.getString("email")
      )
    }
    rs.close()
    items
  }

  def CreateAccount(): Route = path(CreateAccountPath / Segments(3)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.InsertQuery(TableAccountsName, Map("name" -> itemvalues(2), "pass" -> itemvalues(1), "email" -> itemvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def GetAccountById(): Route = path(GetAccountPath / Segment) { userid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.SelectQuery(TableAccountsName, List[String]("id", "name", "email"), ("id", userid))
          val mappedAccounts = mapAccounts(query)
          if(mappedAccounts.nonEmpty) HttpResponse(entity = mappedAccounts.toJson.toString)
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteAccountById(): Route = path(DeleteAccountPath / Segment) { userid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.DeleteQuery(TableAccountsName, Map("id" -> userid))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def UpdateAccountById(): Route = path(UpdateAccountPath / Segments(4)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.UpdateQuery(TableAccountsName, Map("name" -> itemvalues(3), "pass" -> itemvalues(2), "email" -> itemvalues(1)), ("id", itemvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }
}
