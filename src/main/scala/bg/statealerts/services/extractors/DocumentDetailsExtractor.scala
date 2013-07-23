package bg.statealerts.services.extractors

import bg.statealerts.model.Document
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scheduled.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.WebClient

trait DocumentDetailsExtractor {
  /**
   * Fills the document with whatever data can be extracted.
   */
	def populateDocument(doc: Document, row: HtmlElement, ctx: ExtractionContext)
}