package bg.statealerts.scheduled

import org.springframework.http.HttpMethod

case class ExtractorDescriptor(
  sourceName: String,
  extractorType: String,
  tableRowPath: String,
  titlePath: Option[String],
  documentPageTitlePath: Option[String],
  contentPath: Option[String],
  dateFormat: String,
  datePath: Option[String],
  documentPageDatePath: Option[String],
  url: String,
  httpMethod: String,
  flowType: ContentLocationType.Value,
  pagingMultiplier: Int,
  documentLinkPath: Option[String], // XPath to the document link on either the table page, or on the separate document page
  documentPageLinkPath: Option[String]) // XPath to the link to the separate document page, if any
{
}