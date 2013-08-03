package bg.statealerts.services.extractors

import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

import bg.statealerts.model.Document

class DocumentPageExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, rowIdx: Int, ctx: ExtractionContext) = {

    val elements = row.getByXPath(ctx.descriptor.documentLinkPath.get)
    val element = if (elements.size() > 1) elements.get(rowIdx) else elements.get(0)
    var documentUrl: String = element.asInstanceOf[HtmlElement].getAttribute("href");
    if (!documentUrl.startsWith("http")) {
      documentUrl = ctx.baseUrl + documentUrl
    }

    val contentLocationType = ContentLocationType.withName(ctx.descriptor.contentLocationType)
    // if the link is not available in the table, but on a separate page, go to that page first
    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage || contentLocationType == ContentLocationType.LinkedPage) {
      val docPage: HtmlPage = ctx.client.getPage(documentUrl)
      if (ctx.descriptor.documentPageTitlePath.nonEmpty) {
        doc.title = docPage.getFirstByXPath(ctx.descriptor.documentPageTitlePath.get).asInstanceOf[HtmlElement].getTextContent().trim()
      }
      if (ctx.descriptor.documentPageDatePath.nonEmpty) {
        doc.publishDate = ctx.dateFormatter.parseDateTime(docPage.getFirstByXPath(ctx.descriptor.documentPageDatePath.get).asInstanceOf[HtmlElement].getTextContent().trim())
      }

      // if the document is downloadable file, get the link to it. Otherwise set the documentUrl to be the document details page
      if (ctx.descriptor.documentPageLinkPath.nonEmpty) {
	      val link: HtmlElement = docPage.getFirstByXPath(ctx.descriptor.documentPageLinkPath.get);
	      if (link != null) {
	        documentUrl = link.getAttribute("href")
	        if (!documentUrl.startsWith("http")) {
	          documentUrl = ctx.baseUrl + documentUrl
	        }
	      } else {
          documentUrl = null
        }
      }
      doc.url = documentUrl
    }
  }
}