package uk.co.marionete.akka

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{ ActorSystem, Actor }
import akka.testkit.{ TestActors, TestKit, ImplicitSender, EventFilter, TestActorRef, DefaultTimeout }
import org.scalatest.{ WordSpecLike, FlatSpecLike, Matchers, BeforeAndAfterAll }
 
class TestPingPong extends TestKit(ActorSystem("TestPingPong"))
  with Matchers 
  with DefaultTimeout
  with ImplicitSender
  with WordSpecLike {

  val n = 10
  val ping = TestActorRef(new Ping(n))

  "A simple actor" must {
    "update state" in {
      within(5000 millis) {
        ping ! MsgPing
        expectMsg(MsgPing)
        ping.underlyingActor.state.n should be (n - 1)
      }
    }

    "respond with MsgPing when receives MsgPing" in {
      within(5000 millis) {
        ping ! MsgPing
        expectMsg(MsgPing)
      }
    }
  }
}
