package bg.statealerts.scraper

import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

object Utilities {
  def getFullUrl(ctx: bg.statealerts.scraper.ExtractionContext, link: HtmlElement): String = {
    var documentUrl: String = link.getAttribute("href");
    if (!documentUrl.startsWith("http")) {
      if (documentUrl.startsWith("/")) {
        documentUrl = documentUrl.substring(1)
      }
      documentUrl = link.getPage().asInstanceOf[HtmlPage].getFullyQualifiedUrl(documentUrl).toString()
    }
    documentUrl
  }
}