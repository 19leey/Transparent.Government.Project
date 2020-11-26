package tgp.ingestion.module

import akka.actor.{ActorSystem, Props}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import tgp.ingestion.module.Engine.ingestCIDData
import tgp.ingestion.module.Processor.collectCIDUrls


object Main extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  val mongoWorker = system.actorOf(Props[MongoWorker])

  val client: MongoClient = MongoClient()
  val database: MongoDatabase = client.getDatabase("tgp")
  val collection: MongoCollection[Document] = database.getCollection("testcid")

  val states = List("AK", "AL", "AR", "AS", "AZ", "CA", "CO",
    "CT", "DC", "DE", "FL", "GA", "GU", "HI", "IA", "ID",
    "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI",
    "MN", "MO", "MP", "MS", "MT", "NC", "ND", "NE", "NH",
    "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "PR",
    "RI", "SC", "SD", "TN", "TX", "UT", "VA", "VI", "VT",
    "WA", "WI", "WV", "WY")

//  var cids = new ListBuffer[String]()

//  val graph = ingestStates(states)
//  graph.onComplete(done => println("State ingestion complete"))

  val cids = List("N00007999")

  val urls = collectCIDUrls(cids)
  val graph = ingestCIDData(urls)
  graph.onComplete(done => println("CID ingestion complete"))

}