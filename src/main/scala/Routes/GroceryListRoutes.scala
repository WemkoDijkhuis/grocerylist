package Routes

import SQL.SqLQueries
import Util.Logging
import akka.http.scaladsl.model
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

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

  def CreateList(): Route = path(CreateListPath / Segments(2)) { listvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.InsertQuery(TableListName, Map("name" -> listvalues(1), "user_id" -> listvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def GetListsByAccount(): Route = path(GetListsPath / Segment) { userid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.SelectQuery(TableListName, List[String]("id", "name"), ("user_id", userid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def DeleteListById(): Route = path(DeleteListPath / Segment) { listid =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.DeleteQuery(TableListName, Map("id" -> listid))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

  def UpdateListById(): Route = path(UpdateListPath / Segments(2)) { listvalues =>
    pathEndOrSingleSlash {
      get {
        complete {
          SqLQueries.UpdateQuery(TableListName, Map("name" -> listvalues(1)), ("id", listvalues(0)))
          HttpResponse(StatusCodes.NotFound, entity = "Worstenbroodje")
        }
      }
    }
  }

}
