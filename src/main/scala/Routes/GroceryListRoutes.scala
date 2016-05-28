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

case class GetListsParams(listIds: Option[List[String]])

case class CreateListParams(name: String, userId: String)

case class UpdateListParams(name: String, id: String)


object GroceryListRoutes extends Logging {

  implicit val getListsJsonMarshal = jsonFormat1(GetListsParams)
  implicit val createListJsonMarshal = jsonFormat2(CreateListParams)
  implicit val updateListJsonMarshal = jsonFormat2(UpdateListParams)

  val TableListName = "list"

  val GroceryListRouteName = "grocerylists"

  val GetListsPath = "glist"
  val GetMultipleListsPath = "gmlists"
  val CreateListPath = "nlist"
  val DeleteListPath = "dlist"
  val UpdateListPath = "ulist"

  def routes: Route =
    pathPrefix(GroceryListRouteName) {
      GetListsByAccount() ~
        GetListsByListIds() ~
        CreateList() ~
        DeleteListById() ~
        UpdateListById()
    }


  def mapLists(rs: ResultSet): List[Map[String, String]] = {
    var items: List[Map[String, String]] = List[Map[String, String]]()
    while (rs.next()) {
      items = items :+ Map(
        "id" -> rs.getString("id"),
        "name" -> rs.getString("name")
      )
    }
    rs.close()
    items
  }

  def CreateList(): Route = path(CreateListPath) {
    pathEndOrSingleSlash {
      put {
        entity(as[CreateListParams]) { params =>
          complete {
            val query = SqLQueries.InsertQuery(TableListName, Map("name" -> params.name, "user_id" -> params.userId))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
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
          if (mappedLists.nonEmpty) HttpResponse(entity = mappedLists.toJson.toString)
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def GetListsByListIds(): Route = path(GetMultipleListsPath) {
    pathEndOrSingleSlash {
      entity(as[GetListsParams]) { params =>
        post {
          complete {
            val query = SqLQueries.SelectMultipleQuery(TableListName, List[String]("id", "name", "user_id"), ("id", params.listIds.orNull))
            val mappedLists = mapLists(query)
            if (mappedLists.nonEmpty) HttpResponse(entity = mappedLists.toJson.toString)
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
        }
      }
    }
  }

  def DeleteListById(): Route = path(DeleteListPath / Segment) { listid =>
    pathEndOrSingleSlash {
      delete {
        complete {
          val query = SqLQueries.DeleteQuery(TableListName, Map("id" -> listid))
          val numRows = query.toString.toInt
          if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
          else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
        }
      }
    }
  }

  def UpdateListById(): Route = path(UpdateListPath) {
    pathEndOrSingleSlash {
      post {
        entity(as[UpdateListParams]) { params =>
          complete {
            val query = SqLQueries.UpdateQuery(TableListName, Map("name" -> params.name), ("id", params.id))
            val numRows = query.toString.toInt
            if (numRows > 0) HttpResponse(entity = s"${StatusCodes.Success}: num items adjusted = $numRows")
            else HttpResponse(StatusCodes.NotFound, entity = s"${StatusCodes.NotFound}: ${StatusCodes.NotFound.defaultMessage}")
          }
        }
      }
    }
  }
}

