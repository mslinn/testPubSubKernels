package com.x.pubsub

import akka.serialization.SerializationExtension
import akka.zeromq.zeromqSystem
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.zeromq.{Frame, ZMQMessage, Bind, SocketType}
import akka.util.duration._
import java.net.InetAddress
import Resolver._

class PublisherKernel extends Bootable {
  val actorSystemName = "default"

  val strConf = """
                  | stdout-loglevel = "DEBUG"
                  | akka.loglevel = "DEBUG"
                  | akka.actor.debug.receive = on
                  | akka.actor.remote.log-received-messages = on
                  | akka.actor.remote.log-sent-messages = on
                  | akka.remote.netty.hostname = "%s"
                  | akka.remote.netty.port = 2000
                  | """.stripMargin.format(publisherIpAddr)

  val myConfig = ConfigFactory.parseString(strConf)
  val regularConfig = ConfigFactory.load()
  val combined = myConfig.withFallback(regularConfig)
  val complete = ConfigFactory.load(combined)
  val system = ActorSystem(actorSystemName, complete)

  def startup = system.actorOf(Props[TestPublisher], name = "TestPublisher")

  def shutdown = system.shutdown()
}

object Resolver {
  val subscriberIpAddr = InetAddress.getByName("bookviewgeneratoractor").getHostAddress
  val publisherIpAddr = InetAddress.getByName("pubsubbroadcast").getHostAddress
}

class TestPublisher extends Actor with ActorLogging {
  val ser = SerializationExtension(context.system)
  val pubSocket      = context.system.newSocket(SocketType.Pub, Bind("tcp://%s:2012".format(publisherIpAddr)))
  val testSubscriber = context.system.actorFor("akka://default@%s:1994/user/TestSubscriber".format(subscriberIpAddr))

  context.system.scheduler.schedule(100.millis, 100.millis, self, "Tick")

  def receive: Receive = {
     case msg =>
       val heapPayload = ser.serialize("This is a ZeroMQ message").fold(throw _, identity)
       log.debug("TestPublisher about to send ZMQ and Akka messages")
       pubSocket ! ZMQMessage(Seq(Frame("PublisherLifecycle"), Frame(heapPayload)))
       try {
         testSubscriber ! "This is an Akka message"
       } catch {
         case e: Exception =>
           println(e.getMessage)
       }
  }
}
