package bg.statealerts.scraper.config

case class HttpRequest(
  method: Option[String],
  bodyParams: Option[String],
  headers: Option[Map[String, String]],
  warmUpRequest: Option[Boolean],
  warmUpRequestUrl: Option[String]) {
}