package bg.statealerts.services.extractors

import java.net.URL
import java.util.ArrayList

import scala.collection.JavaConversions.asScalaBuffer
import scala.util.control.Breaks

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

import bg.statealerts.model.Document
import bg.statealerts.scheduled.ExtractorDescriptor
import org.apache.commons.lang3.StringUtils
import org.slf4j.{LoggerFactory, Logger}

class Extractor(descriptor: ExtractorDescriptor) {

  val logger: Logger = LoggerFactory.getLogger(classOf[Extractor])

  val tableContentExtractor = new TableContentExtractor()
  val documentPageExtractor = new DocumentPageExtractor()

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(descriptor.dateFormat)

  val pager: Pager = new Pager(descriptor.url, descriptor.bodyParams, descriptor.pagingMultiplier)
  val sourceName = descriptor.sourceName

  val baseUrl: String = {
    val fullUrl = new URL(descriptor.url)
    var port = fullUrl.getPort()
    if (port == -1) {
      port = 80
    }
    fullUrl.getProtocol() + "://" + fullUrl.getHost() + ":" + port
  }

  def extract(since: DateTime): List[Document] = {
    var result = List[Document]()
    val loop = new Breaks();
    val client = buildHtmlClient()
    val httpMethod = HttpMethod.valueOf(descriptor.httpMethod)

    val ctx = new ExtractionContext(descriptor, baseUrl, dateTimeFormatter, client)
    //TODO get from enum field, rather than instantiating for each extraction
    var documentExtractor: DocumentFileExtractor = null
    val docType = DocumentType.withName(descriptor.documentType)
    docType match {
      case DocumentType.PDF => documentExtractor = new PDFDocumentExtractor()
      case DocumentType.DOC => documentExtractor = new PDFDocumentExtractor()
      case DocumentType.HTML => documentExtractor = new HTMLDocumentExtractor()
    }

    // The general flow is as follows:
    // - loop all rows in the table. Continue to the next page (if any). 
    // - loop until there are no more rows or until the date of the row/document is before the "since" parameter
    // - first obtain whatever data is configured from the table itself
    // - depending on "contentLocationType", go to a "details" page and/or parse the content - either HTML, or a downloadable document 
    loop.breakable {
    }
    while (true) {
      val pageUrl = pager.getPageUrl()
      try {
        val request: WebRequest = new WebRequest(new URL(pageUrl), httpMethod)
        // POST parameters are set in the request body
        if (httpMethod == HttpMethod.POST) {
          request.setRequestBody(pager.getBodyParams())
        }
        val htmlPage: HtmlPage = client.getPage(request)
        val list = asScalaBuffer(htmlPage.getByXPath(descriptor.tableRowPath).asInstanceOf[ArrayList[HtmlElement]])
        if (list.isEmpty) {
          loop.break
        }
        var rowIdx = 0
        for (row <- list) {
          // in case there is no way to identify rows by XPath, or in case there is more than one entry per row, use a counter
          val entries = ctx.descriptor.entriesPerRow.getOrElse(1)
          for (i <- 1 to entries) {
            try {
              val doc = new Document()
              doc.sourceName = descriptor.sourceName

              tableContentExtractor.populateDocument(doc, row, rowIdx, ctx)
              if (doc.publishDate != null && doc.publishDate.isBefore(since)) {
                loop.break;
              }

              val contentLocationType = ContentLocationType.withName(descriptor.contentLocationType)
              if (contentLocationType != ContentLocationType.Table) {
                if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage ||
                  contentLocationType == ContentLocationType.LinkedPage) {
                  documentPageExtractor.populateDocument(doc, row, rowIdx, ctx)
                } else if (contentLocationType == ContentLocationType.LinkedDocumentInTable) {
                  doc.url = row.getFirstByXPath(descriptor.documentLinkPath.get).asInstanceOf[HtmlElement].getTextContent();
                }

                if (doc.publishDate != null && doc.publishDate.isBefore(since)) {
                  loop.break;
                }
                if (StringUtils.isNotBlank(doc.url)) {
                  doc.content = documentExtractor.extractContent(doc.url, ctx)
                }
              }
              // don't add empty documents (the content of which was not obtained, for some reason)
              if (StringUtils.isNotBlank(doc.content)) {
                result ::= doc
              }
            } catch {
              case e: Exception => {
                logger.error("Problem parsing page " + pageUrl + " row " + rowIdx, e)
                if (descriptor.failOnError.getOrElse(false)) {
                  loop.break()
                }
              }
            }
            rowIdx += 1
          }
        }
        pager.next()
      } catch {
        case e: Exception => {
          logger.error("Problem parsing page " + pageUrl, e)
          if (descriptor.failOnError.getOrElse(false)) {
            loop.break()
          }
        }
      }
    }
    client.closeAllWindows();
    result
  }

  private def buildHtmlClient(): WebClient = {
    val bvf: Array[BrowserVersionFeatures] = new Array[BrowserVersionFeatures](1)
    bvf(0) = BrowserVersionFeatures.HTMLIFRAME_IGNORE_SELFCLOSING
    val bv: BrowserVersion = new BrowserVersion(BrowserVersion.FIREFOX_17.getApplicationName(),
      BrowserVersion.FIREFOX_17.getApplicationVersion(),
      BrowserVersion.FIREFOX_17.getUserAgent(),
      BrowserVersion.FIREFOX_17.getBrowserVersionNumeric(),
      bvf);
    val client: WebClient = new WebClient(bv)
    client.getOptions().setJavaScriptEnabled(descriptor.javascriptRequired.getOrElse(false))
    client.getOptions.setTimeout(120 * 1000)
    client
  }
}