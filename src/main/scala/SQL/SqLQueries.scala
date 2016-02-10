package SQL

import java.sql.{Connection, DriverManager, ResultSet}
import java.util.concurrent.Executors

import Util.Logging


object SqLQueries extends Logging {

  //TODO set this in a config file
  private val driver = "com.mysql.jdbc.Driver"
  private val url = "jdbc:mysql://192.168.178.21:9002/Boodschappenlijstjes"
  private val username = "BoodschapRestApi"
  private val password = "barfoo"
  val numThreads = 10

  var connection: Connection = null

  def SelectQuery(table: String, fields: List[String] = List[String]("*"), where: (String, String) = ("", ""), order: String = "", extra: String = "") = {
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
