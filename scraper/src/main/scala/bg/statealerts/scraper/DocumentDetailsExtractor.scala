package bg.statealerts.scraper

import bg.statealerts.scraper.model.Document
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scraper.config.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.WebClient

trait DocumentDetailsExtractor {
  /**
   * Fills the document with whatever data can be extracted.
   */
	def populateDocument(doc: Document, row: HtmlElement, rowIdx: Int, ctx: ExtractionContext)
}