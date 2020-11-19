package tgp.ingestion.module

import java.util.UUID

import play.api.libs.json.{JsValue, Json}
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

import scala.collection.Seq
import org.mongodb.scala._
import play.api.libs.json.JsValue.jsValueToJsLookup

case class MongoPayload(collection: MongoCollection[Document], payload: Seq[JsValue])

class MongoWorker extends Actor {
  override def receive: Receive = {
    case MongoPayload(collection, payload) => {
      payload.map {x =>
        val document: Document = Document(x.toString())
        val insertObservable: Observable[Completed] = collection.insertOne(document)

        insertObservable.subscribe(new Observer[Completed] {
          override def onNext(result: Completed): Unit = println(s"onNext: $result")

          override def onError(e: Throwable): Unit = println(s"onError: $e")

          override def onComplete(): Unit = println("onComplete")
        })
      }
    }
    case _       => println("huh?")
  }
}

object Mongo extends App {
    val payload = """{
                    |  "response": {
                    |    "legislator": [
                    |      {
                    |        "@attributes": {
                    |          "cid": "N00038414",
                    |          "firstlast": "Lisa Blunt Rochester",
                    |          "lastname": "Rochester",
                    |          "party": "D",
                    |          "office": "DE01",
                    |          "gender": "F",
                    |          "first_elected": "2016",
                    |          "exit_code": "0",
                    |          "comments": "",
                    |          "phone": "202-225-4165",
                    |          "fax": "",
                    |          "website": "https://bluntrochester.house.gov",
                    |          "webform": "",
                    |          "congress_office": "1123 Longworth House Office Building",
                    |          "bioguide_id": "B001303",
                    |          "votesmart_id": "",
                    |          "feccandid": "",
                    |          "twitter_id": "RepBRochester",
                    |          "youtube_url": "",
                    |          "facebook_id": "Rep.BluntRochester",
                    |          "birthdate": "1962-02-10"
                    |        }
                    |      },
                    |      {
                    |        "@attributes": {
                    |          "cid": "N00012508",
                    |          "firstlast": "Tom Carper",
                    |          "lastname": "Carper",
                    |          "party": "D",
                    |          "office": "DES1",
                    |          "gender": "M",
                    |          "first_elected": "2000",
                    |          "exit_code": "0",
                    |          "comments": "",
                    |          "phone": "202-224-2441",
                    |          "fax": "202-228-2190",
                    |          "website": "https://www.carper.senate.gov/public",
                    |          "webform": "http://www.carper.senate.gov/public/index.cfm/email-senator-carper",
                    |          "congress_office": "513 Hart Senate Office Building",
                    |          "bioguide_id": "C000174",
                    |          "votesmart_id": "22421",
                    |          "feccandid": "S8DE00079",
                    |          "twitter_id": "SenatorCarper",
                    |          "youtube_url": "https://youtube.com/senatorcarper",
                    |          "facebook_id": "tomcarper",
                    |          "birthdate": "1947-01-23"
                    |        }
                    |      },
                    |      {
                    |        "@attributes": {
                    |          "cid": "N00031820",
                    |          "firstlast": "Chris Coons",
                    |          "lastname": "Coons",
                    |          "party": "D",
                    |          "office": "DES2",
                    |          "gender": "M",
                    |          "first_elected": "2010",
                    |          "exit_code": "0",
                    |          "comments": "",
                    |          "phone": "202-224-5042",
                    |          "fax": "202-228-3075",
                    |          "website": "https://www.coons.senate.gov",
                    |          "webform": "https://www.coons.senate.gov/contact",
                    |          "congress_office": "127a Russell Senate Office Building",
                    |          "bioguide_id": "C001088",
                    |          "votesmart_id": "122834",
                    |          "feccandid": "S0DE00092",
                    |          "twitter_id": "SenCoonsOffice",
                    |          "youtube_url": "https://youtube.com/senatorchriscoons",
                    |          "facebook_id": "senatorchriscoons",
                    |          "birthdate": "1963-09-09"
                    |        }
                    |      }
                    |    ]
                    |  }
                    |}""".stripMargin

  val data = Json.parse(payload)
  val legislators = data \\ "@attributes"


  val client: MongoClient = MongoClient()
  val database: MongoDatabase = client.getDatabase("tgp")
  val collection: MongoCollection[Document] = database.getCollection("test")

//  val documents = legislators.map(x => Document(x.toString()))
//  val insertObservable = collection.insertMany(documents)

  val system = ActorSystem("MongoLoader")

  val mongoWorker = system.actorOf(Props[MongoWorker])
  mongoWorker ! MongoPayload(collection, legislators)
  //mongoWorker ! "buenos dias"
}



