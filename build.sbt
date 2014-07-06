import play.PlayScala

name := "playlog"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "jquery" % "1.11.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0-M1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.1.0",
  "mysql" % "mysql-connector-java" % "5.1.23",
  "com.mohiva" %% "play-silhouette" % "1.0",
  "com.google.inject" % "guice" % "4.0-beta",
  "net.codingwell" %% "scala-guice" % "4.0.0-beta",
  cache
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
