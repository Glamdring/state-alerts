package bg.statealerts.scheduled

import bg.statealerts.services.extractors.ContentLocationType
import bg.statealerts.services.extractors.DocumentType

case class ExtractorDescriptor(
  sourceName: String,
  enabled: Option[Boolean],
  documentType: String,
  tableRowPath: String,
  entriesPerRow: Option[Int], // in case there is no way to identify rows by XPath, or in case there is more than one entry per row, use a counter
  titlePath: Option[String],
  documentPageTitlePath: Option[String],
  contentPath: Option[String],
  externalIdPath: Option[String],
  dateFormat: String,
  dateRegex: Option[String], // in case the date is not in a separate field, use regex to locate it
  datePath: Option[String],
  documentPageDatePath: Option[String],
  url: String,
  httpRequest: Option[HttpRequest],
  contentLocationType: String,
  pagingMultiplier: Int,
  documentLinkPath: Option[String], // XPath to the document link on either the table page, or on the separate document page
  documentPageLinkPath: Option[String], // XPath to the link to the separate document page, if any
  javascriptRequired: Option[Boolean],
  failOnError: Option[Boolean]) //whether an error in parsing one document should result in failing the whole batch
{
}