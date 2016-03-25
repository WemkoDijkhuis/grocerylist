package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class CreateAccountParams(name: String, pass: String, email: String)
case class UpdateAccountParams(name: String, pass: String, email: String, id: String)

object GroceryAccountsRoutes extends Logging {

  implicit val createAccountJsonMarshal = jsonFormat3(CreateAccountParams)
  implicit val updateAccountJsonMarshal = jsonFormat4(UpdateAccountParams)

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

  def CreateAccount(): Route = path(CreateAccountPath) {
    pathEndOrSingleSlash {
      put {
        entity(as[CreateAccountParams]) { params =>
          complete {
            val query = SqLQueries.InsertQuery(TableAccountsName, Map("name" -> params.name, "pass" -> params.pass, "email" -> params.email))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
        }
      }
    }
  }

  def GetAccountById(): Route = path(GetAccountPath / Segment) { email =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.SelectQuery(TableAccountsName, List[String]("id", "name", "email"), ("email", email))
          val mappedAccounts = mapAccounts(query)
          if(mappedAccounts.nonEmpty) HttpResponse(entity = mappedAccounts.toJson.toString)
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteAccountById(): Route = path(DeleteAccountPath / Segment) { userid =>
    pathEndOrSingleSlash {
      delete {
        complete {
          val query = SqLQueries.DeleteQuery(TableAccountsName, Map("id" -> userid))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def UpdateAccountById(): Route = path(UpdateAccountPath) {
    pathEndOrSingleSlash {
      post {
        entity(as[UpdateAccountParams]) { params =>
          complete {
            val query = SqLQueries.UpdateQuery(TableAccountsName, Map("name" -> params.name, "pass" -> params.pass, "email" -> params.email), ("id", params.id))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
        }
      }
    }
  }
}
