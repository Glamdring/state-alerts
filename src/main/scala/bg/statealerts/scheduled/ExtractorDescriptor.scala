package bg.statealerts.scheduled

import org.springframework.http.HttpMethod

case class ExtractorDescriptor(
  extractorType: String,
  titlePath: Option[String],
  contentPath: Option[String],
  dateFormat: Option[String],
  datePath: Option[String],
  url: String,
  httpMethod: String,
  pagingMultiplier: Int,
  documentLinkPath: Option[String], // XPath to the document link on either the table page, or on the separate document page
  documentPageLinkPath: Option[String]) // XPath to the link to the separate document page, if any
{
}