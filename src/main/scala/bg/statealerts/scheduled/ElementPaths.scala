package bg.statealerts.scheduled

case class ElementPaths(
  tableRowPath: String,
  titlePath: Option[String],
  documentPageTitlePath: Option[String],
  contentPath: Option[String],
  externalIdPath: Option[String],
  datePath: Option[String],
  documentPageDatePath: Option[String],
  documentLinkPath: Option[String], // XPath to the document link on either the table page, or on the separate document page
  documentPageLinkPath: Option[String]) // XPath to the link to the separate document page, if any
{
}