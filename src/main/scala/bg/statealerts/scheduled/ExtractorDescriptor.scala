package bg.statealerts.scheduled

import bg.statealerts.services.extractors.ContentLocationType
import bg.statealerts.services.extractors.DocumentType

case class ExtractorDescriptor(
  sourceName: String,
  documentType: String,
  tableRowPath: String,
  entriesPerRow: Option[Int], // in case there is no way to identify rows by XPath, or in case there is more than one entry per row, use a counter
  titlePath: Option[String],
  documentPageTitlePath: Option[String],
  contentPath: Option[String],
  dateFormat: String,
  datePath: Option[String],
  documentPageDatePath: Option[String],
  url: String,
  httpMethod: String,
  bodyParams: Option[String],
  contentLocationType: String,
  pagingMultiplier: Int,
  documentLinkPath: Option[String], // XPath to the document link on either the table page, or on the separate document page
  documentPageLinkPath: Option[String], // XPath to the link to the separate document page, if any
  javascriptRequired: Option[Boolean])
{
}