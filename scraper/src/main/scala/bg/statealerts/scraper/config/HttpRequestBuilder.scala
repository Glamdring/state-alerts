package bg.statealerts.scraper.config

class HttpRequestBuilder {
  var method: Option[String] = None
  var bodyParams: Option[String] = None
  var headers: Option[Map[String, String]] = None
  var warmUpRequest: Option[Boolean] = None
  var warmUpRequestUrl: Option[String] = None
  
  def build(): HttpRequest = {
    new HttpRequest(method, bodyParams, headers, warmUpRequest, warmUpRequestUrl)
  }
  
  def setMethod(method: String): HttpRequestBuilder = {
    this.method = Some(method)
    return this
  }
  
  def setBodyParams(bodyParams: String): HttpRequestBuilder = {
    this.bodyParams = Some(bodyParams)
    return this
  }
  
  def setHeaders(headers: java.util.Map[String, String]): HttpRequestBuilder = {
    import scala.collection.JavaConverters._
    this.headers = Some(headers.asScala.toMap[String, String])
    return this
  }
  
  def setWarmUpRequest(warmUpRequest: Boolean): HttpRequestBuilder = {
   this.warmUpRequest  = Some(warmUpRequest)
   return this
  }
  
  def setWarmUpRequestUrl(warmUpRequestUrl: String): HttpRequestBuilder = {
    this.warmUpRequestUrl = Some(warmUpRequestUrl)
    return this
  }
}