import sbtrelease.ReleasePlugin.ReleaseKeys._

organization := "com.gettyimages"

name := "akka-http-swagger"

scalaVersion := "2.11.6"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Maven" at "https://repo1.maven.org/maven2/"

checksums in update := Nil
  
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-RC4"  withSources(),
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0-RC4"  withSources(),
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC4"  withSources(),
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0-RC4" % "test"  withSources(),
  "io.spray"           %% "spray-json"       % "1.3.2" withSources(),
  "com.wordnik" %% "swagger-core" % "1.3.12" excludeAll( ExclusionRule(organization = "org.json4s"),  ExclusionRule(organization="org.fasterxml*") ) withSources(),
  "org.scalatest" %% "scalatest" % "2.2.4" % "test" ,
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "joda-time" % "joda-time" % "2.8",
  "org.joda" % "joda-convert" % "1.7",
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
  