package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json._

object GroceryListRoutes extends Logging {

  val TableListName = "list"

  val GroceryListRouteName = "grocerylists"

  val GetListsPath = "glist"
  val CreateListPath = "nlist"
  val DeleteListPath = "dlist"
  val UpdateListPath = "ulist"

  def routes: Route =
    pathPrefix(GroceryListRouteName) {
      GetListsByAccount() ~
      CreateList() ~
      DeleteListById() ~
      UpdateListById()
    }


  def mapLists(rs: ResultSet): List[Map[String, String]] = {
    var items: List[Map[String, String]] = List[Map[String,String]]()
    while (rs.next()) {
      items = items :+ Map(
        "id" -> rs.getString("id"),
        "name" -> rs.getString("name")
      )
    }
    rs.close()
    items
  }

  def CreateList(): Route = path(CreateListPath / Segments(2)) { listvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.InsertQuery(TableListName, Map("name" -> listvalues(1), "user_id" -> listvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def GetListsByAccount(): Route = path(GetListsPath / Segment) { userid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.SelectQuery(TableListName, List[String]("id", "name"), ("user_id", userid))
          val mappedLists = mapLists(query)
          if(mappedLists.nonEmpty) HttpResponse(entity = mappedLists.toJson.toString)
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteListById(): Route = path(DeleteListPath / Segment) { listid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.DeleteQuery(TableListName, Map("id" -> listid))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def UpdateListById(): Route = path(UpdateListPath / Segments(2)) { listvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.UpdateQuery(TableListName, Map("name" -> listvalues(1)), ("id", listvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

}
