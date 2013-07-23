package bg.statealerts.services.extractors

import bg.statealerts.model.Document
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scheduled.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter
import bg.statealerts.scheduled.ContentLocationType

class TableContentExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, ctx: ExtractionContext) = {

    if (ctx.descriptor.datePath.nonEmpty) {
      doc.publishDate = ctx.dateFormatter.parseDateTime(row.getFirstByXPath(ctx.descriptor.datePath.get).asInstanceOf[HtmlElement].getTextContent())
    }

    if (ctx.descriptor.titlePath.nonEmpty) {
      doc.title = row.getFirstByXPath(ctx.descriptor.titlePath.get).asInstanceOf[HtmlElement].getTextContent()
    }
    if (ctx.descriptor.contentLocationType == ContentLocationType.Table && ctx.descriptor.contentPath.nonEmpty) {
    	doc.content = row.getFirstByXPath(ctx.descriptor.contentPath.get).asInstanceOf[HtmlElement].getTextContent()
    }
  }
}