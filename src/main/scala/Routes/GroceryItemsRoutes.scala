package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json._
import spray.json.DefaultJsonProtocol._

object GroceryItemsRoutes extends Logging {

  val TableItemName = "item"

  val GroceryAccountRouteName = "groceryitems"

  val GetItemPath = "gitem"
  val CreateItemPath = "nitem"
  val DeleteItemPath = "ditem"
  val UpdateItemPath = "uitem"

  def routes: Route =
    pathPrefix(GroceryAccountRouteName) {
      GetItemByList() ~
        CreateItem() ~
        DeleteItemById() ~
        UpdateItemById()
    }

  def mapItems(rs: ResultSet): List[Map[String, String]] = {
    var items: List[Map[String, String]] = List[Map[String,String]]()
    while (rs.next()) {
       items = items :+ Map(
        "id" -> rs.getString("id"),
        "name" -> rs.getString("name"),
        "done_date" -> rs.getDate("done_date").toString
      )
    }
    rs.close()
    items
  }

  def CreateItem(): Route = path(CreateItemPath / Segments(3)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.InsertQuery(TableItemName, Map("name" -> itemvalues(2), "done_date" -> itemvalues(1), "list_id" -> itemvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def GetItemByList(): Route = path(GetItemPath / Segment) { listid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.SelectQuery(TableItemName, List[String]("id", "name", "done_date"), ("list_id", listid))
          val mappedItems = mapItems(query)
          if(mappedItems.nonEmpty) HttpResponse(entity = mappedItems.toJson.toString())
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteItemById(): Route = path(DeleteItemPath / Segment) { itemid =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.DeleteQuery(TableItemName, Map("id" -> itemid))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def UpdateItemById(): Route = path(UpdateItemPath / Segments(3)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          val query = SqLQueries.UpdateQuery(TableItemName, Map("name" -> itemvalues(2), "done_date" -> itemvalues(1)), ("id", itemvalues(0)))
          val numRows = query.toString.toInt
          if(numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }
}
