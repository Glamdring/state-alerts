package bg.statealerts.scraper

import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlElement

class HTMLDocumentExtractor extends DocumentFileExtractor {

  def extractContent(documentUrl: String, ctx: ExtractionContext): String = {
    var docPage: HtmlPage = ctx.client.getPage(documentUrl)
    docPage.getFirstByXPath(ctx.descriptor.paths.contentPath.get).asInstanceOf[HtmlElement].getTextContent()
  }
  
  def extractContent(bytes: Array[Byte], ctx: ExtractionContext): String = {
    new String(bytes, "utf-8")
  }
}