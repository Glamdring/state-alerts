package bg.statealerts.scraper.config

case class Heuristics(
   documentDownloadUrl: String,
   method: String,
   bodyParams: Option[String],
   parameterPath: String,
   parameterRegex: String){
}