package bg.statealerts.scheduled

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

case class HttpRequest(
  method: Option[String],
  bodyParams: Option[String],
  headers: Option[Map[String, String]],
  warmUpRequest: Option[Boolean]) {
}