package bg.statealerts.services.extractors

import com.gargoylesoftware.htmlunit.html.HtmlElement

import bg.statealerts.model.Document

class TableContentExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, ctx: ExtractionContext) = {

    if (ctx.descriptor.datePath.nonEmpty) {
      val element = row.getFirstByXPath(ctx.descriptor.datePath.get).asInstanceOf[HtmlElement]
      if (element == null) {
        throw new IllegalStateException("Cannot find date element for xpath " + ctx.descriptor.titlePath)
      }
      doc.publishDate = ctx.dateFormatter.parseDateTime(element.getTextContent())
    }

    if (ctx.descriptor.titlePath.nonEmpty) {
      val element = row.getFirstByXPath(ctx.descriptor.titlePath.get).asInstanceOf[HtmlElement]
      if (element == null) {
        throw new IllegalStateException("Cannot find title element for xpath " + ctx.descriptor.titlePath)
      }
      doc.title = element.getTextContent()
    }
    if (ctx.descriptor.contentLocationType == ContentLocationType.Table && ctx.descriptor.contentPath.nonEmpty) {
    	doc.content = row.getFirstByXPath(ctx.descriptor.contentPath.get).asInstanceOf[HtmlElement].getTextContent()
    }
  }
}