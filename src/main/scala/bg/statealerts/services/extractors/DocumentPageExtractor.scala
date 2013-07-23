package bg.statealerts.services.extractors

import bg.statealerts.model.Document
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scheduled.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.html.HtmlPage
import bg.statealerts.scheduled.ContentLocationType
import com.gargoylesoftware.htmlunit.WebClient

class DocumentPageExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, ctx: ExtractionContext) = {

    var documentUrl: String = row.getFirstByXPath(ctx.descriptor.documentLinkPath.get).asInstanceOf[HtmlElement].getAttribute("href");
    if (!documentUrl.startsWith("http")) {
      documentUrl = ctx.baseUrl + documentUrl
    }

    // if the link is not available in the table, but on a separate page, go to that page first
    if (ctx.descriptor.contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage || ctx.descriptor.contentLocationType == ContentLocationType.LinkedPage) {
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