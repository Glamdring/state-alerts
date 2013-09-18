package bg.statealerts.scraper.config

/**
 * Builder for easier use in Java applications
 */
class ElementPathsBuilder {
  var tableRowPath: String = _
  var titlePath: Option[String] = None
  var documentPageTitlePath: Option[String] = None
  var contentPath: Option[String] = None
  var externalIdPath: Option[String] = None
  var datePath: Option[String] = None
  var documentPageDatePath: Option[String] = None
  var documentLinkPath: Option[String] = None
  var documentPageLinkPath: Option[String] = None

  def build(): ElementPaths = {
    return new ElementPaths(tableRowPath, titlePath, documentPageTitlePath, contentPath, externalIdPath, 
        datePath, documentPageDatePath, documentLinkPath, documentPageLinkPath)
  }
  
  def setTableRowPath(tableRowPath: String): ElementPathsBuilder = {
    this.tableRowPath = tableRowPath
    return this;
  }

  def setTitlePath(titlePath: String): ElementPathsBuilder = {
    this.titlePath = Some(titlePath)
    return this;
  }

  def setDocumentPageTitlePath(documentPageTitlePath: String): ElementPathsBuilder = {
    this.documentPageTitlePath = Some(documentPageTitlePath)
    return this;
  }

  def setContentPath(contentPath: String): ElementPathsBuilder = {
    this.contentPath = Some(contentPath)
    return this;
  }

  def setDatePath(datePath: String): ElementPathsBuilder = {
    this.datePath = Some(datePath)
    return this;
  }

  def setExternalIdPath(externalIdPath: String): ElementPathsBuilder = {
    this.externalIdPath = Some(externalIdPath)
    return this;
  }

  def setDocumentPageDatePath(documentPageDatePath: String): ElementPathsBuilder = {
    this.documentPageDatePath = Some(documentPageDatePath)
    return this;
  }

  def setDocumentLinkPath(documentLinkPath: String): ElementPathsBuilder = {
    this.documentLinkPath = Some(documentLinkPath)
    return this;
  }

  def setDocumentPageLinkPath(documentPageLinkPath: String): ElementPathsBuilder = {
    this.documentPageLinkPath = Some(documentPageLinkPath)
    return this;
  }
}