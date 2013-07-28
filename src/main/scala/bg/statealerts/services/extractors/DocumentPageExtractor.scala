package bg.statealerts.services.extractors

import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

import bg.statealerts.model.Document

class DocumentPageExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, ctx: ExtractionContext) = {

    var documentUrl: String = row.getFirstByXPath(ctx.descriptor.documentLinkPath.get).asInstanceOf[HtmlElement].getAttribute("href");
    if (!documentUrl.startsWith("http")) {
      documentUrl = ctx.baseUrl + documentUrl
    }

    val contentLocationType = ContentLocationType.withName(ctx.descriptor.contentLocationType)
    // if the link is not available in the table, but on a separate page, go to that page first
    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage || contentLocationType == ContentLocationType.LinkedPage) {
      var docPage: HtmlPage = ctx.client.getPage(documentUrl)
      if (ctx.descriptor.documentPageTitlePath.nonEmpty) {
        doc.title = docPage.getFirstByXPath(ctx.descriptor.documentPageTitlePath.get).asInstanceOf[HtmlElement].getTextContent()
      }
      if (ctx.descriptor.documentPageDatePath.nonEmpty) {
        doc.publishDate = ctx.dateFormatter.parseDateTime(docPage.getFirstByXPath(ctx.descriptor.documentPageDatePath.get).asInstanceOf[HtmlElement].getTextContent())
      }

      // if the document is downloadable file, get the link to it. Otherwise set the documentUrl to be the document details page
      if (ctx.descriptor.documentPageLinkPath.nonEmpty) {
	      var link: HtmlElement = docPage.getFirstByXPath(ctx.descriptor.documentPageLinkPath.get);
	      if (link != null) {
	        documentUrl = link.getAttribute("href")
	        if (!documentUrl.startsWith("http")) {
	          documentUrl = ctx.baseUrl + documentUrl
	        }
	      }
      }
      doc.url = documentUrl
    }
  }
}