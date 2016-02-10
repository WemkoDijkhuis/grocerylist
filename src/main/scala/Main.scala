import Routes.MainRoute
import SQL.SqLQueries
import Util.Logging
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object Main extends App with Logging with MainRoute{
  implicit val system = ActorSystem("rest-api")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  def config(path: String) = system.settings.config.getConfig(path)

  private val host = config("api").getString("host")
  private val port = config("api").getInt("port")

  val binding = Http().bindAndHandle(logRequestResult("log")(routes), host, port)

  binding onFailure {
    case ex: Exception =>
      logger.error(s"FAILED TO BIND TO $host:$port Message: $ex")
  }

  binding onSuccess {
    case _ =>
      logger.debug(s"SUCCESFULLY BIND TO $host:$port")
  }

}
