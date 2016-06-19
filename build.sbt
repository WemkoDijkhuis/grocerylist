import _root_.spray.revolver.RevolverPlugin.Revolver


name := "lijst-rest-api"

version := "1.2.2"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % "2.3.12",
    "com.typesafe.akka" %% "akka-http-experimental"               % "2.0.3",
    "com.typesafe.akka" %% "akka-stream-experimental"             % "2.0.3",
    "com.typesafe.akka" %% "akka-http-core-experimental"          % "2.0.3",
    "com.typesafe.akka" %% "akka-http-experimental"               % "2.0.3",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % "2.0.3",
    "ch.qos.logback"    % "logback-classic"                       % "1.1.2",
    "mysql"             % "mysql-connector-java"                  % "5.1.38",
    "io.spray"          %%  "spray-json"                          % "1.3.2"

  )
}

enablePlugins(JavaAppPackaging)
enablePlugins(SbtNativePackager)

Revolver.settings





