package bg.statealerts.scraper

trait DocumentFileExtractor {

  def extractContent(documentUrl: String, ctx: ExtractionContext): String
  def extractContent(bytes: Array[Byte], ctx: ExtractionContext): String
}