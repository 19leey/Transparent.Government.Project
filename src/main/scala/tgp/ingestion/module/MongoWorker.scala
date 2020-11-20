package tgp.ingestion.module

import java.util.UUID

import play.api.libs.json.{JsObject, JsString, JsValue, Json, __}
import akka.actor.Actor
import java.time.LocalDateTime

import scala.collection.Seq
import org.mongodb.scala._
import play.api.libs.json.JsValue.jsValueToJsLookup

case class MongoPayload(collection: MongoCollection[Document], documentType: String, payload: Seq[JsValue])

class MongoWorker extends Actor {
  override def receive: Receive = {
    case MongoPayload(collection, documentType, payload) => {
      payload.map {x =>
        val strippedCID = x("cid").toString().replace("\"", "")
        val documentId = JsString(documentType + "-" + strippedCID)
        val enrichedPayload = x.as[JsObject] + ("_id" -> documentId)
        val document: Document = Document(enrichedPayload.toString())
        val insertObservable: Observable[Completed] = collection.insertOne(document)

        insertObservable.subscribe(new Observer[Completed] {
          override def onNext(result: Completed): Unit = ()

          override def onError(e: Throwable): Unit = println(s"onError: $e")

          override def onComplete(): Unit = println(s"[${LocalDateTime.now}] Posted document $documentId")
        })
      }
    }
    case _ =>
  }
}
