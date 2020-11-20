package tgp.ingestion.module

import java.time.LocalDateTime

import akka.Done
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.libs.json.Json
import tgp.ingestion.module.Main.{cids, collection, executionContext, mongoWorker, system}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Engine {

  def getCIDs(state: String): Future[List[String]] = {
    val apiKey = "a823a42f77a789a884db54ef9669e48d"
    val url = s"http://www.opensecrets.org/api/?method=getLegislators&apikey=$apiKey&id=$state&output=json"

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    val responseEntity: Future[String] =
      responseFuture.flatMap(_.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")))

    println(s"[${LocalDateTime.now}] Getting CIDs for $state")
    val buffer = responseEntity.flatMap(response => extractCID(response))
    buffer
  }

  def extractCID(response: String): Future[List[String]] = {
    val data = Json.parse(response)
    val cidPayload = data \\ "cid"
    val dataPayload = data \\ "@attributes"
    mongoWorker ! MongoPayload(collection, "legislator", dataPayload)
    val buffer = ListBuffer[String]()
    cidPayload.foreach(buffer += _.toString())
    Future(buffer.toList)
  }

  def ingestStates(states: List[String]): Future[Done] = {
    val source = Source(states)
    val flow = Flow[String].mapAsync(4)(getCIDs(_))
    val sink = Sink.foreach[List[String]](_.foreach(cids += _))
    val graph = source.via(flow).runWith(sink)
    graph
  }

  def makeURLs(cid: String): List[String] = {
    val apiKey = "a823a42f77a789a884db54ef9669e48d"

    val summary = s"http://www.opensecrets.org/api/?method=candSummary&apikey=$apiKey&cid=$cid&output=json"
    val personal = s"http://www.opensecrets.org/api/?method=memPFDprofile&apikey=$apiKey&cid=$cid&output=json"
    val contributors = s"http://www.opensecrets.org/api/?method=candContrib&apikey=$apiKey&cid=$cid&output=json"
    val industries = s"http://www.opensecrets.org/api/?method=candIndustry&apikey=$apiKey&cid=$cid&output=json"
    val sectors = s"http://www.opensecrets.org/api/?method=candSector&apikey=$apiKey&cid=$cid&output=json"
    // TODO MAYBE
    // val url = s"http://www.opensecrets.org/api/?method=candIndByInd&apikey=$apiKey&cid=$cid&output=json"

    println(s"[${LocalDateTime.now}] Generating links for $cid")
    List(summary, personal, contributors, industries, sectors)
  }

  def collectURLs(cids: List[String]): List[String] = {
    val urls = ListBuffer[String]()
    cids.foreach(cid => makeURLs(cid).foreach(urls += _))
    urls.toList
  }

  def getCIDData(url: String): Future[List[String]] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    val responseEntity: Future[String] =
      responseFuture.flatMap(_.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")))

    println(s"[${LocalDateTime.now}] Getting CIDs for $state")
    val buffer = responseEntity.flatMap(response => extractCID(response))
    buffer
  }

}
