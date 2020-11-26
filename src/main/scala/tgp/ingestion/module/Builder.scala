package tgp.ingestion.module

object Builder {
  val apiKey = "a823a42f77a789a884db54ef9669e48d"

  def buildStateUrl(state: String): String = {
    val url = s"http://www.opensecrets.org/api/?method=getLegislators&apikey=$apiKey&id=$state&output=json"
    url
  }

  def buildCIDUrls(cid: String): List[String] = {
    val summary = s"http://www.opensecrets.org/api/?method=candSummary&apikey=$apiKey&cid=$cid&output=json"
    val personal = s"http://www.opensecrets.org/api/?method=memPFDprofile&apikey=$apiKey&cid=$cid&output=json"
    val contributors = s"http://www.opensecrets.org/api/?method=candContrib&apikey=$apiKey&cid=$cid&output=json"
    val industries = s"http://www.opensecrets.org/api/?method=candIndustry&apikey=$apiKey&cid=$cid&output=json"
    val sectors = s"http://www.opensecrets.org/api/?method=candSector&apikey=$apiKey&cid=$cid&output=json"
    // TODO
    // val maybeUrl = s"http://www.opensecrets.org/api/?method=candIndByInd&apikey=$apiKey&cid=$cid&output=json"

    val urls = List(summary, personal, contributors, industries, sectors)
    urls
  }

}
