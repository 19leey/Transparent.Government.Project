package tgp.ingestion.module

import akka.Done
import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json
import tgp.ingestion.module.Main.{cids, collection, executionContext, mongoWorker, system}
import tgp.ingestion.module.Processor.getCIDData

import scala.concurrent.Future
import scala.language.postfixOps

object Engine {

//  def ingestStates(states: List[String]): Future[Done] = {
//    val source = Source(states)
//    val flow = Flow[String].mapAsync(4)(getCIDs(_))
//    val sink = Sink.foreach[List[String]](_.foreach(cids += _))
//    val graph = source.via(flow).runWith(sink)
//    graph
//  }





  def ingestCIDData(urls: List[String]): Future[Done] = {
    val source = Source(urls)
    val flow = Flow[String].mapAsync(8)(getCIDData(_))
    val sink = Sink.foreach[String]{ response =>
      val data = Json.parse(response)
      if(response.contains("summary")) {
        val dataPayload = (data \ "response" \ "summary" \ "@attributes").get
        val rawCID = (data \ "response" \ "summary" \ "@attributes" \ "cid").get
        mongoWorker ! CIDPayload(collection, rawCID, "summary", dataPayload)
      }
      else if(response.contains("member_profile")) {
        val dataPayload = (data \ "response" \ "member_profile").get
        val rawCID = (data \ "response" \ "member_profile" \ "@attributes" \ "member_id").get
        mongoWorker ! CIDPayload(collection, rawCID, "personal", dataPayload)
      }
      else if(response.contains("contributors")) {
        val dataPayload = (data \ "response" \ "contributors").get
        val rawCID = (data \ "response" \ "contributors" \ "@attributes" \ "cid").get
        mongoWorker ! CIDPayload(collection, rawCID, "contributors", dataPayload)
      }
      else if(response.contains("industries")) {
        val dataPayload = (data \ "response" \ "industries").get
        val rawCID = (data \ "response" \ "industries" \ "@attributes" \ "cid").get
        mongoWorker ! CIDPayload(collection, rawCID, "industries", dataPayload)
      }
      else if(response.contains("sectors")) {
        val dataPayload = (data \ "response" \ "sectors").get
        val rawCID = (data \ "response" \ "sectors" \ "@attributes" \ "cid").get
        mongoWorker ! CIDPayload(collection, rawCID, "sectors", dataPayload)
      }
    }
    val graph = source.via(flow).runWith(sink)
    graph
  }

}
