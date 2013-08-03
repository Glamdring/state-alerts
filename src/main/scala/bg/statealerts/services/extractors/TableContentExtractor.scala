package bg.statealerts.services.extractors

import com.gargoylesoftware.htmlunit.html.HtmlElement

import bg.statealerts.model.Document

class TableContentExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, rowIdx: Int, ctx: ExtractionContext) = {

    // if there is more than one element that matches the XPath, use the rowIdx (some tables do not have proper row separation, so the idx may be needed)
    if (ctx.descriptor.datePath.nonEmpty) {
      val elements = row.getByXPath(ctx.descriptor.datePath.get)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find date element for xpath " + ctx.descriptor.datePath)
      }
      val element = if (elements.size() > 1) elements.get(rowIdx) else elements.get(0)
      doc.publishDate = ctx.dateFormatter.parseDateTime(element.asInstanceOf[HtmlElement].getTextContent())
    }

    if (ctx.descriptor.titlePath.nonEmpty) {
      val elements = row.getByXPath(ctx.descriptor.titlePath.get)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find title element for xpath " + ctx.descriptor.titlePath)
      }
      val element = if (elements.size() > 1) elements.get(rowIdx) else elements.get(0)
      doc.title = element.asInstanceOf[HtmlElement].getTextContent()
    }
    //TODO use rowIdx
    if (ctx.descriptor.contentLocationType == ContentLocationType.Table && ctx.descriptor.contentPath.nonEmpty) {
    	doc.content = row.getFirstByXPath(ctx.descriptor.contentPath.get).asInstanceOf[HtmlElement].getTextContent()
    }
  }
}