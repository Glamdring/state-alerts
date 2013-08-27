package bg.statealerts.scraper.config

import scala.beans.BeanProperty

case class ExtractorDescriptor(
  @BeanProperty sourceKey: String,
  @BeanProperty sourceDisplayName: Option[String],
  enabled: Option[Boolean],
  documentType: String,
  entriesPerRow: Option[Int], // in case there is no way to identify rows by XPath, or in case there is more than one entry per row, use a counter
  paths: ElementPaths,
  dateFormat: String,
  dateRegex: Option[String], // in case the date is not in a separate field, use regex to locate it
  url: String,
  httpRequest: Option[HttpRequest],
  heuristics: Option[Heuristics],
  contentLocationType: String,
  pagingMultiplier: Int,
  javascriptRequired: Option[Boolean],
  failOnError: Option[Boolean]) //whether an error in parsing one document should result in failing the whole batch
{
}