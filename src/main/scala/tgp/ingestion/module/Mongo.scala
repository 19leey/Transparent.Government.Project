package tgp.ingestion.module

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.mongodb.scaladsl.MongoSink
import akka.stream.scaladsl.Source
import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoClients
import play.api.libs.json.Json


object Mongo extends App {

  private val client = MongoClients.create("mongodb://localhost:27017")
  private val db = client.getDatabase("test")

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val paylaod = """{
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
  val data = Json.parse(paylaod)
  val legislators = data \\ "legislator"
  println(legislators)


}