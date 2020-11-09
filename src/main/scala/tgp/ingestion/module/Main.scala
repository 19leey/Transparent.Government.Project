package tgp.ingestion.module

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer

object Main extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher


  def getCIDs(state: String): Future[List[String]] = {
    val apiKey = "a823a42f77a789a884db54ef9669e48d"
    val url = s"http://www.opensecrets.org/api/?method=getLegislators&apikey=$apiKey&id=$state&output=json"

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    val responseEntity: Future[String] =
      responseFuture.flatMap(_.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")))

    val buffer = responseEntity.flatMap(response => extractCID(response))
    buffer
  }

  def extractCID(response: String): Future[List[String]] = {
    // @TODO save the responses to somewhere - mongoDB????
    val data = Json.parse(response)
    val payload = data \\ "cid"
    val buffer = ListBuffer[String]()
    payload.foreach(cid => buffer += cid.toString())
    Future(buffer.toList)
  }

  val states = List("AK", "AL", "AR", "AS", "AZ", "CA", "CO",
    "CT", "DC", "DE", "FL", "GA", "GU", "HI", "IA", "ID",
    "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI",
    "MN", "MO", "MP", "MS", "MT", "NC", "ND", "NE", "NH",
    "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "PR",
    "RI", "SC", "SD", "TN", "TX", "UT", "VA", "VI", "VT",
    "WA", "WI", "WV", "WY")

  var cids = new ListBuffer[String]()

  val source = Source(states)
  val flow = Flow[String].mapAsync(4)(x => getCIDs(x))
  val sink = Sink.foreach[List[String]](_.foreach(cid => cids += cid.toString))
  val graph = source.via(flow).runWith(sink)
  graph.onComplete(done => println(cids))

  // cids - will be new source
}