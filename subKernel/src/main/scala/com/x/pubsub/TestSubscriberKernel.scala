package com.x.pubsub

import akka.kernel.Bootable
import akka.actor._
import akka.serialization.SerializationExtension
import akka.zeromq._
import com.typesafe.config.ConfigFactory
import java.net.InetAddress
import Resolver._

class TestSubscriberKernel extends Bootable {
  val actorSystemName = "default"

  val strConf = """
                  | stdout-loglevel = "DEBUG"
                  | akka.loglevel = "DEBUG"
                  | akka.actor.debug.receive = on
                  | akka.actor.remote.log-received-messages = on
                  | akka.actor.remote.log-sent-messages = on
                  | akka.remote.netty.hostname = "%s"
                  | akka.remote.netty.port = 1994
                  | """.stripMargin.format(subscriberIpAddr)

  val myConfig = ConfigFactory.parseString(strConf)
  val regularConfig = ConfigFactory.load()
  val combined = myConfig.withFallback(regularConfig)
  val complete = ConfigFactory.load(combined)
  val system = ActorSystem(actorSystemName, complete)

  def startup = system.actorOf(Props[TestSubscriber], "TestSubscriber")

  def shutdown = system.shutdown()
}

object Resolver {
  val subscriberIpAddr = InetAddress.getByName("bookviewgeneratoractor").getHostAddress
  val publisherIpAddr = InetAddress.getByName("pubsubbroadcast").getHostAddress
}

class TestSubscriber extends Actor with ActorLogging {
  val ser = SerializationExtension(context.system)
  context.system.newSocket(SocketType.Sub, Listener(self), Connect("tcp://%s:2012".format(publisherIpAddr)), Subscribe("PublisherLifecycle"))

  def receive: Receive = {
     case msg =>
       log.debug("TestSubscriber got '" + msg + "' [" + msg.getClass.getName + "]")
  }
}
