import _root_.spray.revolver.RevolverPlugin.Revolver

name := "lijst-rest-api"

version := "0.1"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % "2.3.11",
    "com.typesafe.akka" %% "akka-http-experimental"               % "2.0.1",
    "com.typesafe.akka" %% "akka-stream-experimental"             % "2.0.1",
    "com.typesafe.akka" %% "akka-http-core-experimental"          % "2.0.1",
    "com.typesafe.akka" %% "akka-http-experimental"               % "2.0.1",
    "ch.qos.logback"    % "logback-classic"                       % "1.1.2",
    "mysql"             % "mysql-connector-java"                  % "5.1.38"

  )
}

Revolver.settings





