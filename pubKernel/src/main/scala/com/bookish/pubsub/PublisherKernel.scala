package com.x.pubsub

import akka.serialization.SerializationExtension
import akka.zeromq.zeromqSystem
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.zeromq.{Frame, ZMQMessage, Bind, SocketType}
import akka.util.duration._
//import java.net.InetAddress
//import Resolver._

class TestPublisher extends Actor with ActorLogging {
  val ser = SerializationExtension(context.system)
  val pubSocket = context.system.newSocket(SocketType.Pub, Bind("tcp://127.0.0.1:1235"))

  context.system.scheduler.schedule(1000.millis, 1000.millis, self, "Tick")

  def receive: Receive = {
     case msg =>
       log.debug("TestPublisher about to send ZeroMQ message")
       val payload = ser.serialize("This is a ZeroMQ message").fold(throw _, identity)
       pubSocket ! ZMQMessage(Seq(Frame("PublisherLifecycle"), Frame(payload)))
  }
}

class PublisherKernel extends Bootable {
  val actorSystemName = "default"

  val strConf = """
                  | akka.stdout-loglevel = "DEBUG"
                  | akka.loglevel = "DEBUG"
                  | akka.actor.debug.receive = on
                  | akka.actor.remote.log-received-messages = on
                  | akka.actor.remote.log-sent-messages = on
                  | akka.event-handlers = ["akka.event.slf4j.Slf4jEventHandler"]
                  | #akka.remote.netty.hostname = "%s"
                  | #akka.remote.netty.port = 2000
                  | """.stripMargin//.format(publisherIpAddr)

  val myConfig = ConfigFactory.parseString(strConf)
  val regularConfig = ConfigFactory.load()
  val combined = myConfig.withFallback(regularConfig)
  val complete = ConfigFactory.load(combined)
  val system = ActorSystem(actorSystemName, complete)

//  println("publisherIpAddr=" + publisherIpAddr)
//  println("subscriberIpAddr=" + subscriberIpAddr)

  def startup = system.actorOf(Props[TestPublisher], name = "publisher")

  def shutdown = system.shutdown()
}

//object Resolver {
//  val subscriberIpAddr = InetAddress.getByName("subscriber").getHostAddress
//  val publisherIpAddr = InetAddress.getByName("publisher").getHostAddress
//}
