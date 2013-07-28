package bg.statealerts.scheduled

import bg.statealerts.services.extractors.ContentLocationType
import bg.statealerts.services.extractors.DocumentType

case class ExtractorDescriptor(
  sourceName: String,
  documentType: String,
  tableRowPath: String,
  titlePath: Option[String],
  documentPageTitlePath: Option[String],
  contentPath: Option[String],
  dateFormat: String,
  datePath: Option[String],
  documentPageDatePath: Option[String],
  url: String,
  httpMethod: String,
  contentLocationType: String,
  pagingMultiplier: Int,
  documentLinkPath: Option[String], // XPath to the document link on either the table page, or on the separate document page
  documentPageLinkPath: Option[String], // XPath to the link to the separate document page, if any
  javascriptRequired: Option[Boolean])
{
}