package bg.statealerts.services.extractors

trait DocumentFileExtractor {

  def extractContent(documentUrl: String): String
}