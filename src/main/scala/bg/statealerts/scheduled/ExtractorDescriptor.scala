package bg.statealerts.scheduled

case class ExtractorDescriptor(
  extractorType: String,
  titlePath: String = null,
  contentPath: String = null,
  dateFormat: String = null,
  datePath: String = null,
  url: String) {
}