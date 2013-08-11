package bg.statealerts.scheduled

case class Heuristics(
   documentDownloadUrl: String,
   method: String,
   bodyParams: Option[String],
   parameterPath: String,
   parameterRegex: String){
}