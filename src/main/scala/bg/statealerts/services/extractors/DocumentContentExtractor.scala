package bg.statealerts.services.extractors

import bg.statealerts.model.Document
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scheduled.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter

trait DocumentContentExtractor {
  /**
   * Fills the document with whatever data can be extracted.
   */
	def populateDocument(doc: Document, row: HtmlElement, descriptor: ExtractorDescriptor, baseUrl: String, formatter: DateTimeFormatter)
}