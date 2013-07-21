package bg.statealerts.services.extractors

import bg.statealerts.model.Document
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scheduled.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter

class TableContentExtractor extends DocumentContentExtractor {

  def populateDocument(doc: Document, row: HtmlElement, descriptor: ExtractorDescriptor, baseUrl: String, formatter: DateTimeFormatter) = {

    if (descriptor.datePath.nonEmpty) {
      doc.publishDate = formatter.parseDateTime(row.getFirstByXPath(descriptor.datePath.get).asInstanceOf[HtmlElement].getTextContent())
    }

    if (descriptor.titlePath.nonEmpty) {
      doc.title = row.getFirstByXPath(descriptor.titlePath.get).asInstanceOf[HtmlElement].getTextContent()
    }
  }
}