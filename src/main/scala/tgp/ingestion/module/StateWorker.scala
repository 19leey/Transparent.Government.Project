package tgp.ingestion.module

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import tgp.ingestion.module.CIDWorker._
import tgp.ingestion.module.StateWorker._

import scala.concurrent.Future

object StateWorker {

  case class State(state: String)

}

class StateWorker extends Actor {
  val apiKey = "a823a42f77a789a884db54ef9669e48d"

  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case State(state) =>
      val url = s"http://www.opensecrets.org/api/?method=getLegislators&apikey=$apiKey&id=$state&output=json"
      println(s"STATE WORKER: $state URL: $url")



      val cidWorker = context.actorOf(Props[CIDWorker])
      val cids = List("N00007360", "N00004367", "N00006863")
      cids.foreach(x => cidWorker ! CID(x, state))

    case Done() =>
      context.parent ! Done
      context.stop(self)

    case _ =>
      println("FAILURE")
  }

}
