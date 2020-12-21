package tgp.ingestion.module

import java.time.LocalDateTime

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import tgp.ingestion.module.Builder.{buildCIDUrls, buildStateUrl}
import tgp.ingestion.module.Main.{collection, executionContext, mongoWorker, system}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Processor {
  def getCIDs(state: String): Future[List[String]] = {
    val url = buildStateUrl(state)
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
    mongoWorker ! StatePayload(collection, dataPayload)
    val buffer = ListBuffer[String]()
    cidPayload.foreach(buffer += _.toString())
    Future(buffer.toList)
  }

  def collectCIDUrls(cids: List[String]): List[String] = {
    var urls = ListBuffer[String]()
    cids.foreach(cid => buildCIDUrls(cid).foreach(urls += _))
    urls.toList
  }

  def getData(url: String): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    val responseEntity: Future[String] =
      responseFuture.flatMap(_.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")))

    println(s"[${LocalDateTime.now}] Getting CID data for $url")
//    responseEntity.flatMap(response => formatPayload(response))
    responseEntity
//    responseEntity.flatMap(response => extractData(response))
  }

  def formatPayload(response: String): Unit = {
    if(response.contains("summary")) {
      mongoWorker ! SummaryPayload(collection, response)
    }
    else if(response.contains("member_profile")) {
      mongoWorker ! PersonalPayload(collection, response)
    }
    else if(response.contains("sectors")) {
      mongoWorker ! SectorsPayload(collection, response)
    }
    else if(response.contains("contributors")) {
      mongoWorker ! ContributorsPayload(collection, response)
    }
    else if(response.contains("industries")) {
      mongoWorker ! IndustriesPayload(collection, response)
    }
    else {
      println("INVALID")
    }
  }

  def extractData(response: String): Unit = {
    val payload = formatPayload(response)
    println(payload)
//    mongoWorker ! TestPayload(collection, payload)


  }

}
