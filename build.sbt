import play.PlayScala

name := "playlog"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "1.0",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "jquery" % "1.11.0",
  "net.codingwell" %% "scala-guice" % "4.0.0-beta4",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.5",
  "mysql" % "mysql-connector-java" % "5.1.32",
  /*"com.mohiva" %% "play-html-compressor" % "0.3.1",*/
  "com.mohiva" %% "play-html-compressor" % "0.4-SNAPSHOT",
  cache,
  filters
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"
 
LessKeys.compress in Assets := true
