name := """playlog"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1",
  "mysql" % "mysql-connector-java" % "5.1.23"
)

play.Project.playScalaSettings