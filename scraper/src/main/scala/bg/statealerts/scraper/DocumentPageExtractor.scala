package bg.statealerts.scraper

import java.util.regex.Pattern
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import bg.statealerts.scraper.model.Document
import bg.statealerts.scraper.config.ContentLocationType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DocumentPageExtractor extends DocumentDetailsExtractor {

  val logger: Logger = LoggerFactory.getLogger(classOf[DocumentPageExtractor])

  def populateDocument(doc: Document, row: HtmlElement, rowIdx: Int, ctx: ExtractionContext) = {

    val elements = row.getByXPath(ctx.descriptor.paths.documentLinkPath.get)
    val element = if (elements.size() > 1) elements.get(rowIdx) else elements.get(0)
    var documentUrl = Utilities.getFullUrl(ctx, element.asInstanceOf[HtmlElement])

    val contentLocationType = ContentLocationType.withName(ctx.descriptor.contentLocationType)
    // if the link is not available in the table, but on a separate page, go to that page first
    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage || contentLocationType == ContentLocationType.LinkedPage) {
	  logger.debug("Requesting document page: " + documentUrl)
      val docPage: HtmlPage = ctx.client.getPage(documentUrl)
	  
      if (ctx.descriptor.paths.documentPageTitlePath.nonEmpty) {
        doc.title = docPage.getFirstByXPath(ctx.descriptor.paths.documentPageTitlePath.get).asInstanceOf[HtmlElement].getTextContent().trim()
      }
      if (ctx.descriptor.paths.documentPageDatePath.nonEmpty) {
        val element = docPage.getFirstByXPath(ctx.descriptor.paths.documentPageDatePath.get).asInstanceOf[HtmlElement]
        if (element != null) {
	        var text = element.getTextContent().trim()
	        if (ctx.descriptor.dateRegex.nonEmpty) {
	          val pattern = Pattern.compile(ctx.descriptor.dateRegex.get)
	          val matcher = pattern.matcher(text)
	          if (matcher.find()) {
	            text = matcher.group()
	          }
	        }
	        doc.publishDate = ctx.dateFormatter.parseDateTime(text).withTimeAtStartOfDay()
        }
      }

      // if the document is downloadable file, get the link to it. Otherwise set the documentUrl to be the document details page
      if (ctx.descriptor.paths.documentPageLinkPath.nonEmpty) {
        val link: HtmlElement = docPage.getFirstByXPath(ctx.descriptor.paths.documentPageLinkPath.get);
        if (link != null) {
          documentUrl = Utilities.getFullUrl(ctx, link)
        } else {
          documentUrl = null
        }
      }
      doc.url = documentUrl
 
      if (ctx.descriptor.paths.metaDataUrlPath.nonEmpty) {
        val link: HtmlElement = docPage.getFirstByXPath(ctx.descriptor.paths.metaDataUrlPath.get);
        if (link != null) {
          doc.metaDataUrl = Utilities.getFullUrl(ctx, link)
        }
      }
      
      if (ctx.descriptor.paths.additionalMetaDataPaths.nonEmpty) {
        val metaData = scala.collection.mutable.Map[String, String]()
        for (path <- ctx.descriptor.paths.additionalMetaDataPaths.get) {
          val value = docPage.getFirstByXPath(path._2)
          if (value != null) {
          	metaData.put(path._1, value)
          }
        }
        doc.additionalMetaData = metaData.toMap;
      }
    }
  }
}