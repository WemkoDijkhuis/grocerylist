package Routes

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

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

  def CreateItem(): Route = path(CreateItemPath / Segments(3)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.InsertQuery(TableItemName, Map("name" -> itemvalues(2), "done_date" -> itemvalues(1), "list_id" -> itemvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def GetItemByList(): Route = path(GetItemPath / Segment) { listid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.SelectQuery(TableItemName, List[String]("id", "name", "done_date"), ("list_id", listid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def DeleteItemById(): Route = path(DeleteItemPath / Segment) { itemid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.DeleteQuery(TableItemName, Map("id" -> itemid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def UpdateItemById(): Route = path(UpdateItemPath / Segments(3)) { itemvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.UpdateQuery(TableItemName, Map("name" -> itemvalues(2), "done_date" -> itemvalues(1)), ("id", itemvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }
}
