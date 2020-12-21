package tgp.ingestion.module

import java.util.UUID

import play.api.libs.json.{JsObject, JsString, JsValue, Json, __}
import akka.actor.Actor
import java.time.LocalDateTime

import scala.collection.Seq
import org.mongodb.scala._
import org.w3c.dom.DocumentType
import play.api.libs.json.JsValue.jsValueToJsLookup

//summary = s"http://www.opensecrets.org/api/?method=candSummary&apikey=$apiKey&cid=$cid&output=json"
//val personal = s"http://www.opensecrets.org/api/?method=memPFDprofile&apikey=$apiKey&cid=$cid&output=json"
//val contributors = s"http://www.opensecrets.org/api/?method=candContrib&apikey=$apiKey&cid=$cid&output=json"
//val industries = s"http://www.opensecrets.org/api/?method=candIndustry&apikey=$apiKey&cid=$cid&output=json"
//val sectors

case class StatePayload(collection: MongoCollection[Document], payload: Seq[JsValue])
case class SummaryPayload(collection: MongoCollection[Document], response: String)
case class PersonalPayload(collection: MongoCollection[Document], response: String)
case class ContributorsPayload(collection: MongoCollection[Document], response: String)
case class IndustriesPayload(collection: MongoCollection[Document], response: String)
case class SectorsPayload(collection: MongoCollection[Document], response: String)


case class CIDPayload(collection: MongoCollection[Document], rawCID: JsValue, documentType: String, payload: JsValue)
case class TestPayload(collection: MongoCollection[Document], payload: JsValue)

class MongoWorker extends Actor {
  override def receive: Receive = {
    case StatePayload(collection, payload) => {
      payload.map {x =>
        val strippedCID = x("cid").toString().replace("\"", "")
        val documentId = JsString("legislator-" + strippedCID)
        val enrichedPayload = x.as[JsObject] + ("_id" -> documentId)
        val document: Document = Document(enrichedPayload.toString())
        val insertObservable: Observable[Completed] = collection.insertOne(document)

        insertObservable.subscribe(new Observer[Completed] {
          override def onNext(result: Completed): Unit = ()

          override def onError(e: Throwable): Unit = println(s"Error: $e")

          override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
        })
      }
    }
    case CIDPayload(collection, rawCID, documentType, payload) => {
      val strippedCID = rawCID.toString().replace("\"", "")
      val documentId = JsString(documentType + "-" + strippedCID)
      val enrichedPayload = payload.as[JsObject] + ("_id" -> documentId)
      val document: Document = Document(enrichedPayload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
      })
    }
    case SummaryPayload(collection, response) => {
      val data = Json.parse(response)
      val dataPayload = (data \ "response" \ "summary" \ "@attributes").get
      val rawCID = (data \ "response" \ "summary" \ "@attributes" \ "cid").get
      val strippedCID = rawCID.toString().replace("\"", "")
      val documentId = JsString("summary-" + strippedCID)
      val payload = dataPayload.as[JsObject] + ("_id" -> documentId)

      val document: Document = Document(payload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
      })
    }
    case PersonalPayload(collection, response) => {
      val data = Json.parse(response)
      val dataPayload = (data \ "response" \ "member_profile").get
      val rawCID = (data \ "response" \ "member_profile" \ "@attributes" \ "member_id").get
      val strippedCID = rawCID.toString().replace("\"", "")
      val documentId = JsString("personal-" + strippedCID)
      val payload = dataPayload.as[JsObject] + ("_id" -> documentId)

      val document: Document = Document(payload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
      })
    }
    case ContributorsPayload(collection, response) => {
      val data = Json.parse(response)
      val dataPayload = (data \ "response" \ "contributors").get
      val rawCID = (data \ "response" \ "contributors" \ "@attributes" \ "cid").get
      val strippedCID = rawCID.toString().replace("\"", "")
      val documentId = JsString("contributors-" + strippedCID)
      val payload = dataPayload.as[JsObject] + ("_id" -> documentId)

      val document: Document = Document(payload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
      })
    }
    case IndustriesPayload(collection, response) => {
      val data = Json.parse(response)
      val dataPayload = (data \ "response" \ "industries").get
      val rawCID = (data \ "response" \ "industries" \ "@attributes" \ "cid").get
      val strippedCID = rawCID.toString().replace("\"", "")
      val documentId = JsString("industries-" + strippedCID)
      val payload = dataPayload.as[JsObject] + ("_id" -> documentId)

      val document: Document = Document(payload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
      })
    }
    case SectorsPayload(collection, response) => {
      val data = Json.parse(response)
      val dataPayload = (data \ "response" \ "sectors").get
      val rawCID = (data \ "response" \ "sectors" \ "@attributes" \ "cid").get
      val strippedCID = rawCID.toString().replace("\"", "")
      val documentId = JsString("sectors-" + strippedCID)
      val payload = dataPayload.as[JsObject] + ("_id" -> documentId)

      val document: Document = Document(payload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
      })
    }
    case TestPayload(collection, payload) => {
      val document: Document = Document(payload.toString())

      val insertObservable: Observable[Completed] = collection.insertOne(document)

      insertObservable.subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = ()

        override def onError(e: Throwable): Unit = println(s"Error: $e")

        override def onComplete(): Unit = println(s"[${LocalDateTime.now}]")
      })
    }
  }
}
