package tgp.ingestion.module

import akka.Done
import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.{JsObject, Json}
import tgp.ingestion.module.Main.{cids, collection, data, executionContext, mongoWorker, system}
import tgp.ingestion.module.Processor.{formatPayload, getData}

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
    val flow = Flow[String].mapAsync(8)(getData(_))
    val sink = Sink.foreach[String](formatPayload(_))
    val graph = source.via(flow).runWith(sink)
    graph
  }

}
