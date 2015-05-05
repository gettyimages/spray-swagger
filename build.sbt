organization := "com.tecsisa"

name := "akka-http-swagger"

scalaVersion := "2.11.6"


lazy val apiProject = (project in file("."))
  .settings(
    libraryDependencies ++= {
      val AkkaVersion = "2.3.9"
      val AkkaStreamVersion = "1.0-M5"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
        "com.typesafe.akka" %% "akka-http-testkit-experimental" % AkkaStreamVersion,
        "com.typesafe.akka" %% "akka-stream-experimental" % AkkaStreamVersion,
        "com.typesafe.akka" %% "akka-http-experimental" % AkkaStreamVersion,
        "org.scalatest" %% "scalatest" % "2.2.4" % "test",
        "com.wordnik" %% "swagger-core" % "1.3.12" excludeAll(ExclusionRule(organization = "org.json4s"), ExclusionRule(organization = "org.fasterxml*")),
        "org.json4s" %% "json4s-jackson" % "3.2.11",
        "org.json4s" %% "json4s-native" % "3.2.11",
        "joda-time" % "joda-time" % "2.7",
        "org.joda" % "joda-convert" % "1.7",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
        "javax.ws.rs" % "jsr311-api" % "1.1.1"
      )
    })


parallelExecution in Test := false

publishArtifact in Test := false

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
