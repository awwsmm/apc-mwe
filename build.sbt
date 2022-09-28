ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"

lazy val root = (project in file("."))
  .settings(
    name := "apc-mwe",
    libraryDependencies := Seq(
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.0.6",
      "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "1.0.6"
    )
  )