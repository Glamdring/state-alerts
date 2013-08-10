package bg.statealerts.scheduled

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

case class HttpRequest(
  method: String,
  bodyParams: Option[String],
  headers: Option[Map[String, String]]) {
}