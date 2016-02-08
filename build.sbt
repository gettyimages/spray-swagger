import sbtrelease.ReleasePlugin.ReleaseKeys._

organization := "com.gettyimages"

name := "akka-http-swagger"

scalaVersion := "2.11.7"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Maven" at "https://repo1.maven.org/maven2/"

checksums in update := Nil
  
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core-experimental" % "2.0.3"  withSources(),
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.0.3"  withSources(),
  "com.typesafe.akka" %% "akka-http-experimental" % "2.0.3"  withSources(),
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.0.3" % "test"  withSources(),
  "io.spray"           %% "spray-json"       % "1.3.2" withSources(),
  "io.swagger" % "swagger-scala-module_2.11" % "1.0.1" withSources(),
  "io.swagger" % "swagger-jaxrs" % "1.5.7" withSources(),
  "org.scalatest" %% "scalatest" % "2.2.6" % "test" ,
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.json4s" %% "json4s-native" % "3.3.0",
  "joda-time" % "joda-time" % "2.9.2",
  "org.joda" % "joda-convert" % "1.8.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "javax.ws.rs" % "jsr311-api" % "1.1.1"
)

releaseSettings

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

parallelExecution in Test := false

homepage := Some(url("https://github.com/gettyimages/akka-http-swagger"))

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

net.virtualvoid.sbt.graph.Plugin.graphSettings

publishArtifactsAction := PgpKeys.publishSigned.value

pomExtra := (
  <scm>
    <url>git@github.com:gettyimages/akka-http-swagger.git</url>
    <connection>scm:git:git@github.com:gettyimages/akka-http-swagger.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mhamrah</id>
      <name>Michael Hamrah</name>
      <url>http://michaelhamrah.com</url>
    </developer>
    <developer>
      <id>efuquen</id>
      <name>Edwin Fuquen</name>
      <url>http://parascal.com</url>
    </developer>
  </developers>)

publishTo := Some("Artifactory Realm" at "https://artifactory.gluware.com:8443/artifactory/glue-infastructure/")

credentials += Credentials(Path.userHome / ".artifactory_credentials")
  