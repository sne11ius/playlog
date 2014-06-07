name := """playlog"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1",
  "mysql" % "mysql-connector-java" % "5.1.23",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.1.0",
  "ws.securesocial" %% "securesocial" % "2.1.3"
)

resolvers += Resolver.url(
  "SBT Plugin Releases",
  url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

play.Project.playScalaSettings
