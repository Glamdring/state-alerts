package bg.statealerts.scraper.config

class ExtractorDescriptorBuilder {
  var sourceKey: String = _
  var sourceDisplayName: Option[String] = None
  var enabled: Option[Boolean] = None
  var documentType: String = _
  var entriesPerRow: Option[Int] = None
  var paths: ElementPaths = _
  var dateFormat: String = _
  var dateRegex: Option[String] = None // in case the date is not in a separate field, use regex to locate it
  var url: String = _
  var httpRequest: Option[HttpRequest] = None
  var heuristics: Option[Heuristics] = None
  var contentLocationType: String = _
  var pagingMultiplier: Int = _
  var javascriptRequired: Option[Boolean] = None
  var failOnError: Option[Boolean] = None

  def build(): ExtractorDescriptor = {
    return new ExtractorDescriptor(sourceKey, sourceDisplayName, enabled, documentType, entriesPerRow, paths, dateFormat, dateRegex,
      url, httpRequest, heuristics, contentLocationType, pagingMultiplier, javascriptRequired, failOnError)
  }
  
  def setSourceKey(sourceKey: String): ExtractorDescriptorBuilder = {
    this.sourceKey = sourceKey
    return this;
  }

  def setSourceDisplayName(sourceDisplayName: String): ExtractorDescriptorBuilder = {
    this.sourceDisplayName = Some(sourceDisplayName)
    return this;
  }

  def setEnabled(enabled: Boolean): ExtractorDescriptorBuilder = {
    this.enabled = Some(enabled)
    return this;
  }

  def setDocumentType(documentType: String): ExtractorDescriptorBuilder = {
    this.documentType = documentType
    return this;
  }

  def setEntriesPerRow(entriesPerRow: Int): ExtractorDescriptorBuilder = {
    this.entriesPerRow = Some(entriesPerRow)
    return this;
  }

  def setSourceKey(paths: ElementPaths): ExtractorDescriptorBuilder = {
    this.paths = paths
    return this;
  }

  def setDateFormat(dateformat: String): ExtractorDescriptorBuilder = {
    this.dateFormat = dateFormat
    return this;
  }

  def setDateRegex(dateRegex: String): ExtractorDescriptorBuilder = {
    this.dateRegex = Some(dateRegex)
    return this;
  }

  def setUrl(url: String): ExtractorDescriptorBuilder = {
    this.url = url
    return this;
  }

  def setHttpRequest(httpRequest: HttpRequest): ExtractorDescriptorBuilder = {
    this.httpRequest = Some(httpRequest)
    return this;
  }

  def setHeuristics(heuristics: Heuristics): ExtractorDescriptorBuilder = {
    this.heuristics = Some(heuristics)
    return this;
  }

  def setContentLocationType(contentLocationType: String): ExtractorDescriptorBuilder = {
    this.contentLocationType = contentLocationType
    return this;
  }

  def setPagingMultiplier(pagingMultiplier: Int): ExtractorDescriptorBuilder = {
    this.pagingMultiplier = pagingMultiplier
    return this;
  }

  def setJavascriptRequired(javascriptRequired: Boolean): ExtractorDescriptorBuilder = {
    this.javascriptRequired = Some(javascriptRequired)
    return this;
  }

  def setFailOnError(failOnError: Boolean): ExtractorDescriptorBuilder = {
    this.failOnError = Some(failOnError)
    return this;
  }
}