package Routes

import java.sql.ResultSet

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json._


case class CreateItemParams(name: String, doneDate: String,  listId: String)
case class UpdateItemParams(name: String, doneDate: String, id: String)

object GroceryItemsRoutes extends Logging {

  implicit val createItemJsonMarshal = jsonFormat3(CreateItemParams)
  implicit val updateItemJsonMarshal = jsonFormat3(UpdateItemParams)

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
    var items: List[Map[String, String]] = List[Map[String, String]]()
    while (rs.next()) {
      items = items :+ Map(
        "id" -> rs.getString("id"),
        "name" -> rs.getString("name"),
        "done_date" -> rs.getTimestamp("done_date").toString
      )
    }
    rs.close()
    items
  }

  def CreateItem(): Route = path(CreateItemPath) {
    pathEndOrSingleSlash {
      put {
        entity(as[CreateItemParams]) { params =>
          complete {
            val query = SqLQueries.InsertQuery(TableItemName, Map("name" -> params.name, "done_date" -> params.doneDate, "list_id" -> params.listId))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
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
          if (mappedItems.nonEmpty) HttpResponse(entity = mappedItems.toJson.toString())
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def DeleteItemById(): Route = path(DeleteItemPath / Segment) { itemid =>
    pathEndOrSingleSlash {
      delete {
        complete {
          val query = SqLQueries.DeleteQuery(TableItemName, Map("id" -> itemid))
          val numRows = query.toString.toInt
          if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def UpdateItemById(): Route = path(UpdateItemPath) {
    pathEndOrSingleSlash {
      post {
        entity(as[UpdateItemParams]) { params =>
          complete {
            val query = SqLQueries.UpdateQuery(TableItemName, Map("name" -> params.name, "done_date" -> params.doneDate), ("id", params.id))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
        }
      }
    }
  }
}
