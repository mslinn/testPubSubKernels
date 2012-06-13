import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{Dist, outputDirectory, distJvmOptions}

object TestSubscriberBuild extends Build {
  val Organization = "com.x"
  val Version = "2.0.1"
  val ScalaVersion = "2.9.1"

  lazy val TestSubscriberKernel = Project(
    id = "Pubsub test subscriber kernel",
    base = file("."),
    settings = defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Dependencies.testSubscriberKernel,
      distJvmOptions in Dist := "-Xms256M -Xmx1024M -Djava.library.path=/usr/local/lib",
      outputDirectory in Dist := file("target/test-subscriber-dist")
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
      "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"),

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
  )
}

object Dependencies {

  import Dependency._

  val testSubscriberKernel = Seq(
    akkaKernel, akkaRemote, akkaSlf4j, logback, zmqAkka, zmq
  )
}

object Dependency {
  object V {
    val Akka = "2.0.1"
  }

  val akkaKernel = "com.typesafe.akka"  % "akka-kernel"                % V.Akka
  val akkaRemote = "com.typesafe.akka" %  "akka-remote"                % V.Akka
  val akkaSlf4j  = "com.typesafe.akka"  % "akka-slf4j"                 % V.Akka
  val logback    = "ch.qos.logback"     % "logback-classic"            % "1.0.0"
  val zmq        = "org.zeromq"         % "zeromq-scala-binding_2.9.1" % "0.0.5"
  val zmqAkka    = "com.typesafe.akka"  % "akka-zeromq"                % V.Akka
}
