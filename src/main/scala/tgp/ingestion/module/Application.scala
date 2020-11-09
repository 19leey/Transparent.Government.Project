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

object Application extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher


  val cids = List("N00007999", "N00035774", "N00026050", "N00035380", "N00030768", "N00024759",
    "N00003028", "N00030910", "N00035691", "N00030622", "N00024817", "N00009920", "N00030770",
    "N00035792", "N00031857", "N00035527", "N00033363", "N00013873", "N00007635", "N00037515",
    "N00029260", "N00025284", "N00030771", "N00039293", "N00006460", "N00036097", "N00042056",
    "N00041750", "N00033982", "N00033983", "N00033987", "N00033030", "N00030856", "N00006863",
    "N00007419", "N00027459", "N00030717", "N00034224", "N00026926", "N00040853", "N00030709",
    "N00007360", "N00008046", "N00029649", "N00033508", "N00026341", "N00026427", "N00007335",
    "N00007479", "N00038601", "N00028122", "N00007248", "N00028152", "N00037015", "N00040644",
    "N00044298", "N00034254", "N00030600", "N00009585", "N00033373", "N00006897", "N00033997",
    "N00006789", "N00035825", "N00040597", "N00036107", "N00033510", "N00031877", "N00024870",
    "N00041464", "N00006671", "N00006701", "N00007099", "N00006690", "N00037019", "N00040865",
    "N00037260", "N00033274", "N00040666", "N00040667", "N00029258", "N00007021", "N00033591",
    "N00009604", "N00036915", "N00007364", "N00006134", "N00041080", "N00027509", "N00030829",
    "N00028133", "N00040876", "N00027510", "N00030608", "N00030780", "N00000575", "N00024842",
    "N00000615", "N00029070", "N00043421", "N00027566", "N00031685", "N00001692", "N00038414",
    "N00012508", "N00031820", "N00039503", "N00037442", "N00033220", "N00039777", "N00030642",
    "N00042403", "N00040133", "N00029662", "N00037422", "N00033449", "N00026335", "N00027462",
    "N00002942", "N00027514", "N00043319", "N00027626", "N00042808", "N00037269", "N00040007",
    "N00002884", "N00002893", "N00031317", "N00026106", "N00030650", "N00025337", "N00041561",
    "N00042811", "N00043290", "N00030612", "N00035346", "N00002674", "N00039090", "N00027848",
    "N00002577", "N00042813", "N00032416", "N00032457", "N00033518", "N00032243", "N00035347",
    "N00033720", "N00024871", "N00030788", "N00035516", "N00002593", "N00046125", "N00042619",
    "N00025882", "N00033281", "N00028138", "N00028139", "N00040888", "N00027741", "N00041104",
    "N00025237", "N00001758", "N00035483", "N00041335", "N00006263", "N00029441", "N00006267",
    "N00004887", "N00035215", "N00027239", "N00042114", "N00030581", "N00041338", "N00004884",
    "N00033240", "N00004724", "N00033101", "N00029139", "N00035420", "N00034784", "N00041569",
    "N00004961", "N00030667", "N00033390", "N00037031", "N00004981", "N00027860", "N00003813",
    "N00031226", "N00037185", "N00041954", "N00033495", "N00041956", "N00029513", "N00031227",
    "N00038429", "N00041731", "N00030670", "N00037034", "N00042126", "N00042626", "N00040712",
    "N00005285", "N00005282", "N00038260", "N00029675", "N00028073", "N00034041", "N00003473",
    "N00031233", "N00003389", "N00030836", "N00009660", "N00030184", "N00039953", "N00039106",
    "N00036633", "N00036135", "N00030245", "N00026823", "N00000153", "N00000179", "N00041808",
    "N00034044", "N00035278", "N00035431", "N00042581", "N00013855", "N00031933", "N00033492",
    "N00000270", "N00029147", "N00025482", "N00027751", "N00036999", "N00001821", "N00039122",
    "N00001971", "N00001799", "N00037036", "N00001955", "N00013820", "N00013817", "N00041668",
    "N00034580", "N00000491", "N00039533", "N00030673", "N00031938", "N00036275", "N00033395",
    "N00004133", "N00026368", "N00041357", "N00042149", "N00036274", "N00040915", "N00036149",
    "N00042649", "N00034068", "N00029277", "N00004118", "N00031390", "N00037039", "N00041134",
    "N00012942", "N00043581", "N00035440", "N00004558", "N00041511", "N00042353", "N00027500",
    "N00012460", "N00033106", "N00030026", "N00031005", "N00026790", "N00013323", "N00030676",
    "N00035282", "N00005195", "N00041620", "N00030418", "N00037003", "N00003288", "N00042458",
    "N00031958", "N00043298", "N00003280", "N00040733", "N00027605", "N00033054", "N00027035",
    "N00033399", "N00002299", "N00044027", "N00002260", "N00026166", "N00035311", "N00033527",
    "N00033630", "N00044335", "N00026627", "N00033631", "N00035451", "N00039551", "N00035492",
    "N00002221", "N00042868", "N00031688", "N00004614", "N00026631", "N00037049", "N00027623",
    "N00033443", "N00035544", "N00042161", "N00030875", "N00038397", "N00024790", "N00036154",
    "N00042164", "N00041370", "N00009816", "N00036944", "N00000781", "N00041843", "N00027523",
    "N00000751", "N00034639", "N00041154", "N00036158", "N00000699", "N00035267", "N00040933",
    "N00042467", "N00029562", "N00029835", "N00006561", "N00030191", "N00031177", "N00037247",
    "N00033638", "N00033638", "N00038734", "N00037161", "N00029404", "N00001193", "N00038742",
    "N00035927", "N00001171", "N00034547", "N00001102", "N00033640", "N00026961", "N00000939",
    "N00041588", "N00000078", "N00034549", "N00041162", "N00001813", "N00001003", "N00001024",
    "N00034277", "N00040741", "N00030196", "N00035523", "N00041385", "N00030949", "N00035934",
    "N00043207", "N00027060", "N00044575", "N00001285", "N00027658", "N00001093", "N00003689",
    "N00033310", "N00033904", "N00027894", "N00012233", "N00032088", "N00031128", "N00038767",
    "N00003522", "N00025175", "N00030490", "N00042194", "N00025280", "N00035007", "N00029574",
    "N00041690", "N00003535", "N00003682", "N00040829", "N00033410", "N00005559", "N00025726",
    "N00041394", "N00031129", "N00005582", "N00033474", "N00007690", "N00007727", "N00007781",
    "N00030071", "N00029303", "N00007724", "N00038779", "N00035307", "N00038450", "N00042894",
    "N00042706", "N00040949", "N00041997", "N00034128", "N00029416", "N00034120", "N00038781",
    "N00031777", "N00044065", "N00043242", "N00041871", "N00029736", "N00031647", "N00041870",
    "N00001373", "N00001489", "N00027503", "N00037615", "N00032019", "N00009724", "N00027533",
    "N00000362", "N00041400", "N00024809", "N00030752", "N00042715", "N00027783", "N00002408",
    "N00033832", "N00031782", "N00009975", "N00040559", "N00004572", "N00035187", "N00028463",
    "N00041594", "N00030815", "N00030957", "N00003132", "N00041599", "N00041873", "N00025445",
    "N00003225", "N00003105", "N00009888", "N00026148", "N00042224", "N00027709", "N00035972",
    "N00042237", "N00042240", "N00041194", "N00005883", "N00026686", "N00026460", "N00026041",
    "N00008799", "N00006052", "N00033539", "N00038809", "N00041702", "N00031545", "N00005818",
    "N00038285", "N00033316", "N00042268", "N00029285", "N00031417", "N00026710", "N00030602",
    "N00025219", "N00041882", "N00024978", "N00042282", "N00008122", "N00025095", "N00040989",
    "N00033839", "N00034349", "N00006023", "N00005736", "N00024852", "N00033085", "N00025292",
    "N00033932", "N00041221", "N00042013", "N00000286", "N00031696", "N00029459", "N00042293",
    "N00002147", "N00039327", "N00043541", "N00042296", "N00041418", "N00036018", "N00032029",
    "N00041002", "N00029891", "N00033177", "N00002097", "N00035000", "N00000515", "N00000528",
    "N00009918", "N00030693", "N00009759", "N00031559", "N00036403", "N00026314", "N00034453",
    "N00038858", "N00041606", "N00007833", "N00031557", "N00007836", "N00007876", "N00043379",
    "N00033549", "N00004403", "N00026914", "N00004291", "N00036409", "N00030967", "N00045307",
    "N00039330", "N00004367", "N00032546", "N00031681", "N00033814", "N00041542", "N00032838",
    "N00009771", "N00035504", "N00006236", "N00006249")


    def makeURLs(cid: String): List[String] = {
      val apiKey = "a823a42f77a789a884db54ef9669e48d"

      val summary = s"http://www.opensecrets.org/api/?method=candSummary&apikey=$apiKey&cid=$cid&output=json"
      val personal = s"http://www.opensecrets.org/api/?method=memPFDprofile&apikey=$apiKey&cid=$cid&output=json"
      val contributors = s"http://www.opensecrets.org/api/?method=candContrib&apikey=$apiKey&cid=$cid&output=json"
      val industries = s"http://www.opensecrets.org/api/?method=candIndustry&apikey=$apiKey&cid=$cid&output=json"
      val sectors = s"http://www.opensecrets.org/api/?method=candSector&apikey=$apiKey&cid=$cid&output=json"
      // TODO MAYBE
      // val url = s"http://www.opensecrets.org/api/?method=candIndByInd&apikey=$apiKey&cid=$cid&output=json"

      List(summary, personal, contributors, industries, sectors)
    }

    val urls = ListBuffer[String]()
    cids.foreach(cid => makeURLs(cid).foreach(urls += _))

    print(urls.size)


}