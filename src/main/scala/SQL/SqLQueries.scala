package SQL

import java.io.File
import java.sql.{Connection, DriverManager, ResultSet}
import java.util.concurrent.Executors

import Util.Logging
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory


object SqLQueries extends Logging {
  implicit val system = ActorSystem("rest-api")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  //This gives a Nullpointer but in the main class it works :S
//  val config = ConfigFactory.parseFile(new File(getClass.getResource("sql.conf").getPath))
  def config(path: String) = system.settings.config.getConfig(path)

  //TODO set this in a config file
  private val driver = "com.mysql.jdbc.Driver"
  private val url = s"jdbc:mysql://${config("api.sqlsettings").getString("host")}:${config("api.sqlsettings").getString("port")}/${config("api.sqlsettings").getString("database_name")}"
  private val username = config("api.sqlsettings").getString("username")
  private val password = config("api.sqlsettings").getString("password")
  val numThreads = 10

  var connection: Connection = null

  def SelectQuery(table: String, fields: List[String] = List[String]("*"), where: (String, String) = ("", ""), order: String = "", extra: String = "") = {
    logger.debug("hallo?")
    var query = s"SELECT ${fields.mkString(", ")} FROM $table "

    if (where._1 != "") query = query + s"WHERE ${where._1} = \'${where._2}\' "
    if (order != "") query = query + s"ORDER BY $order "
    if (extra != "") query = query + extra

    ExecuteQuery(query)
  }

  def InsertQuery(table: String, values: Map[String, String]) = {
    var query = s"INSERT INTO $table "
    var columnList = List[String]()
    var valuesList = List[String]()

    for (value <- values) {
      columnList = columnList :+ value._1
      valuesList = valuesList :+ value._2
    }
    query = query + s"(${columnList.mkString(", ")}) "
    query = query + s"VALUES (\'${valuesList.mkString("\', \'")}\') "

    ExecuteUpdate(query)
  }

  def UpdateQuery(table: String, values: Map[String, String], where: (String, String)) = {
    var query = s"UPDATE $table "
    var valuesList = List[String]()

    for (value <- values) {
      valuesList = valuesList :+ s"${value._1} = \'${value._2}\'"
    }
    query = query + s"SET ${valuesList.mkString(", ")} "

    if (where._1 != "") query = query + s"WHERE ${where._1} = \'${where._2}\' "

    ExecuteUpdate(query)
  }

  def DeleteQuery(table: String, values: Map[String, String]) = {
    var query = s"DELETE FROM $table "
    var valuesList = List[String]()

    for (value <- values) {
      valuesList = valuesList :+ s"${value._1} = \'${value._2}\'"
    }
    query = query + s"WHERE ${valuesList.mkString(" AND ")} "

    ExecuteUpdate(query)
  }


  def ExecuteUpdate(query: String) = {
    try {
      logger.info(s"Update query has been fired.")
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      connection.setNetworkTimeout(Executors.newFixedThreadPool(numThreads), 30000)

      val statement = connection.createStatement()
      statement.executeUpdate(query)
    }
    catch {
      case e: Exception =>
        logger.error(s"Could not excecute query to mysql: $e")
        connection.createStatement().executeQuery("")
    } finally {
      connection.close()
    }
  }

  def ExecuteQuery(query: String): ResultSet = {
    try {
      logger.info(s"Select query has been fired.")
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      connection.setNetworkTimeout(Executors.newFixedThreadPool(numThreads), 30000)

      val statement = connection.createStatement()
      statement.executeQuery(query)
    }
    catch {
      case e: Exception =>
        logger.error(s"Could not excecute query to mysql: $e")
        connection.createStatement().executeQuery("")
    }
  }
}
