package tgp.ingestion.module

import java.util.UUID

import akka.actor.Actor
import akka.actor.typed.scaladsl.adapter.ClassicActorContextOps
import tgp.ingestion.module.CIDWorker._

object CIDWorker {

  case class Done()
  case class CID(cid: String, state: String)
  case class getCandidateSummary(cid: String)
  case class getCandidateContributors(cid: String)
  case class getCandidateIndustries(cid: String)

}

class CIDWorker extends Actor {
  val apiKey = "a823a42f77a789a884db54ef9669e48d"

  override def receive: Receive = {
    case CID(cid, state) =>
      val url = s"http://www.opensecrets.org/api/?method=memPFDprofile&apikey=$apiKey&cid=$cid&output=json"
      println(s"CID WORKER CREATED BY: $state")
      println(s"CID: $cid URL: $url")

    case _ =>
      println("FAILURE")
      stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }

}
