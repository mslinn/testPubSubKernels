package com.x.pubsub

import akka.actor._
import akka.serialization.SerializationExtension
import akka.zeromq._
import akka.kernel.Bootable

class SubscriberKernel extends Bootable {
  implicit val system = ActorSystem()

  def startup = system.actorOf(Props[TestSubscriber], name = "subscriber")

  def shutdown = system.shutdown()
}

class TestSubscriber extends Actor with ActorLogging {
  context.system.newSocket(SocketType.Sub, Listener(self), Connect("tcp://127.0.0.1:1235"), Subscribe(""))
  val ser = SerializationExtension(context.system)

  def receive: Receive = {
    // the first frame is the topic, second is the message
    case m: ZMQMessage =>
      log.debug("TestSubscriber got a ZMQMessage")
      ser.deserialize(m.payload(1), classOf[String]) match {
        case Right(str) =>
          println("  " + str)

        case Left(e) =>
          throw e
      }

    case msg =>
       log.debug("TestSubscriber got an unexpected '" + msg + "' [" + msg.getClass.getName + "]")
  }
}
