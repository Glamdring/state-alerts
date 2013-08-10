package bg.statealerts.services.extractors

trait DocumentFileExtractor {

  def extractContent(documentUrl: String, ctx: ExtractionContext): String
  def extractContent(bytes: Array[Byte], ctx: ExtractionContext): String
}