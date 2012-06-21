import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{Dist, outputDirectory, distJvmOptions}

object PublisherBuild extends Build {
  val Organization = "com.x"
  val Version = "0.0.2"
  val ScalaVersion = "2.9.2"

  lazy val BroadcastKernel = Project(
    id = "Minimal publisher kernel",
    base = file("."),
    settings = defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Dependencies.publisherKernel,
      distJvmOptions in Dist := "-Xms256M -Xmx1024M -Djava.library.path=/usr/local/lib",
      outputDirectory in Dist := file("target/publisher-dist")
    )
  )

  val IvyXML =
    <dependencies>
      <exclude org="org.slf4j" module="slf4j-jdk14"/>
    </dependencies>

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    crossPaths := false,
    organizationName := "X, LLC.",
    organizationHomepage := Some(url("http://www.x.com")),
    ivyXML := IvyXML
  )
  lazy val defaultSettings = buildSettings ++ Seq(
    resolvers ++= Seq(
      "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
    ),

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
  )
}

object Dependencies {
  import Dependency._

  val publisherKernel = Seq(
    akkaActor, akkaKernel, akkaRemote, akkaSlf4j, akkaZMQ, logback, zeroMQ
  )
}

object Dependency {
  object V {
    val Akka = "2.0.2"
  }

  val akkaActor  = "com.typesafe.akka"  % "akka-actor"                 % V.Akka  withSources()
  val akkaKernel = "com.typesafe.akka"  % "akka-kernel"                % V.Akka  withSources()
  val akkaRemote = "com.typesafe.akka" %  "akka-remote"                % V.Akka  withSources()
  val akkaSlf4j  = "com.typesafe.akka"  % "akka-slf4j"                 % V.Akka  withSources()
  val akkaZMQ    = "com.typesafe.akka"  % "akka-zeromq"                % "latest.integration"  withSources()
  val logback    = "ch.qos.logback"     % "logback-classic"            % "1.0.0" withSources()
  val zeroMQ     = "org.zeromq"         % "zeromq-scala-binding_2.9.1" % "0.0.6" withSources()
}
