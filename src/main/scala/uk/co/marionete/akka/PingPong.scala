package uk.co.marionete.akka

import java.io._
import akka.actor._
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.util.Random
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory

sealed trait Msg
case object MsgPing extends Msg
case object MsgSartGame extends Msg
case object MsgResumeGame extends Msg
case class MsgEndGame(name: String) extends Msg

class Ping(n: Int) extends Actor {
  case class State(n: Int, other: Option[String] = None)
  var internalState = new State(n)
  def state = internalState

  def receive = {
    case MsgSartGame => 
      context.actorSelection("/user/Ping") ! MsgPing
    case MsgPing if internalState.n == 0 =>
      context.actorSelection("/user/Terminator") ! MsgEndGame(self.path.toSerializationFormat)
    case MsgPing =>
      internalState = internalState.copy(internalState.n - 1, Some(sender.path.toSerializationFormat))
      println(s"Player is: ${self.path.toSerializationFormat}, state is ${internalState.n}")
        sender ! MsgPing
  }
}

class Terminator extends Actor {
  def receive = {
    case MsgEndGame(name) =>
      println(s"Game over $name")
      context.system.terminate()
  }
}

object Main extends App {
  val maxGame = 1000
  game(Random.nextInt(maxGame), Random.nextInt(maxGame))

  def game(nPing: Int, nPong: Int): Unit = {
    val system = ActorSystem("PingPongSystem")

    val terminator = system actorOf(Props[Terminator], name = "Terminator")
    val ping = system actorOf(Props(new Ping(nPing)), name = "Ping")
    val pong = system actorOf(Props(new Ping(nPong)), name = "Pong")

    ping ! MsgSartGame
  }
}
