package bg.statealerts.scraper

import java.net.URL
import java.util.ArrayList
import scala.collection.JavaConversions.asScalaBuffer
import scala.util.control.Breaks
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.ReadableDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import bg.statealerts.scraper.model.Document
import bg.statealerts.scraper.config.ExtractorDescriptor
import scala.annotation.meta.param
import java.util.regex.Pattern
import scala.annotation.meta.param
import com.gargoylesoftware.htmlunit.WebWindowListener
import scala.beans.BeanProperty
import bg.statealerts.scraper.config.DocumentType
import bg.statealerts.scraper.config.ContentLocationType
import scala.collection.JavaConversions
import java.util.Locale
import org.w3c.dom.Attr

class Extractor(@BeanProperty val descriptor: ExtractorDescriptor) {

  val logger: Logger = LoggerFactory.getLogger(classOf[Extractor])

  val tableContentExtractor = new TableContentExtractor()
  val documentPageExtractor = new DocumentPageExtractor()

  var dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(descriptor.dateFormat)
  descriptor.dateLocale.foreach(l => dateTimeFormatter = dateTimeFormatter.withLocale(new Locale(l)))
  
  val pager: Pager = new Pager(descriptor.url, descriptor.httpRequest.flatMap(_.bodyParams), descriptor.pagingMultiplier, descriptor.firstPage)
  
  var baseUrl: String = {
    val fullUrl = new URL(descriptor.url)
    var port = fullUrl.getPort()
    if (port == -1) {
      port = 80
    }
    fullUrl.getProtocol() + "://" + fullUrl.getHost() + ":" + port + "/"
  }
  
  val client = buildHtmlClient()
  
  var htmlPage: HtmlPage = _
  
  //TODO get from enum field, rather than instantiating for each extraction
  val documentExtractor: DocumentFileExtractor = {
    val docType = DocumentType.withName(descriptor.documentType)
    docType match {
      case DocumentType.PDF => new PDFDocumentExtractor()
      case DocumentType.DOC => new PDFDocumentExtractor()
      case DocumentType.HTML => new HTMLDocumentExtractor()
    }
  }

  def extractDocuments(since: ReadableDateTime): java.util.List[Document] = {
    return JavaConversions.seqAsJavaList(extract(since))
  }
  
  def extract(since: ReadableDateTime): List[Document] = {
    if (!descriptor.enabled.getOrElse(true)) {
      return List()
    }
    val today = new DateTime().withTimeAtStartOfDay()
    
    var result = List[Document]()
    val loop = new Breaks()
    val httpMethod = HttpMethod.valueOf(descriptor.httpRequest.map(_.method.getOrElse("GET")).getOrElse("GET"))
    descriptor.httpRequest.foreach(_.headers.foreach(map => {
      map.foreach(e => client.addRequestHeader(e._1, e._2))
    }))
    val ctx = new ExtractionContext(descriptor, baseUrl, dateTimeFormatter, client)

    // warm-up request: in case some cookies/session need to be populated first
    descriptor.httpRequest.foreach { req => 
		req.warmUpRequest.foreach(if (_) client.getPage(req.warmUpRequestUrl.getOrElse(baseUrl)))
	}

    // The general flow is as follows:
    // - loop all rows in the table. Continue to the next page (if any). 
    // - loop until there are no more rows or until the date of the row/document is before the "since" parameter
    // - first obtain whatever data is configured from the table itself
    // - depending on "contentLocationType", go to a "details" page and/or parse the content - either HTML, or a downloadable document 
    loop.breakable {
      while (true) {
        val pageUrl = pager.getPageUrl()
        val pageBodyParams = pager.getBodyParams()
        try {
          logger.debug("Requesting page: " + pageUrl + "[" + pageBodyParams + "]")
          val request: WebRequest = new WebRequest(new URL(pageUrl), httpMethod)
          // POST parameters are set in the request body
          if (httpMethod == HttpMethod.POST) {
            request.setRequestBody(pageBodyParams)
          }

          htmlPage = client.getPage(request)
          val list = asScalaBuffer(htmlPage.getByXPath(descriptor.paths.tableRowPath).asInstanceOf[ArrayList[HtmlElement]])

          if (list.isEmpty) {
            loop.break
          }
          for (row <- list) {
            // in case there is no way to identify rows by XPath, or in case there is more than one entry per row, use a counter
            val entries = ctx.descriptor.entriesPerRow.getOrElse(1)
            for (entryIdx <- 0 until entries) {
              try {
                val doc = new Document()
                doc.sourceKey = descriptor.sourceKey
                doc.sourceDisplayName = descriptor.sourceDisplayName.getOrElse(descriptor.sourceKey)

                tableContentExtractor.populateDocument(doc, row, entryIdx, ctx)
                if (doc.publishDate != null && !doc.publishDate.isAfter(since)) {
                  loop.break
                }

                val contentLocationType = ContentLocationType.withName(descriptor.contentLocationType)
                if (contentLocationType != ContentLocationType.Table) {
                  if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage ||
                    contentLocationType == ContentLocationType.LinkedPage) {
                    documentPageExtractor.populateDocument(doc, row, entryIdx, ctx)
                  } else if (contentLocationType == ContentLocationType.LinkedDocumentInTable) {
                    getLinkedDocument(doc, row, ctx, request)
                  }

                  if (doc.publishDate != null && !doc.publishDate.isAfter(since)) {
                    loop.break
                  }
                  if (StringUtils.isNotBlank(doc.url)) {
                    doc.content = documentExtractor.extractContent(doc.url, ctx)
                  }
                }
                // don't add empty documents (the content of which was not obtained, for some reason)
                // also, do not import documents from the current day, as new ones may appear later, and normally we don't get the hour of upload, only the date
                if (StringUtils.isNotBlank(doc.content) && doc.publishDate != null && doc.publishDate.isBefore(today)) {
                  result ::= doc
                }
              } catch {
                case e: Exception => {
                  logger.error("Problem parsing page " + pageUrl + "[" + pageBodyParams + "] row " + entryIdx, e)
                  if (descriptor.failOnError.getOrElse(false)) {
                    result = List() //failing - no documents are to be stored
                    loop.break
                  }
                }
              }
            }
          }
          // no paging required
          if (descriptor.pagingMultiplier == 0) {
            loop.break
          }
          pager.next()
        } catch {
          case e: Exception => {
            logger.error("Problem parsing page " + pageUrl + "[" + pageBodyParams + "]", e)
            if (descriptor.failOnError.getOrElse(false)) {
              result = List() //failing - no documents are to be stored
            }
            loop.break // always break in this "catch", otherwise endless loops will occur
          }
        }
      }
    }
    client.closeAllWindows()
    result
  }

  private def getLinkedDocument(doc: Document, row: HtmlElement, ctx: ExtractionContext, request: WebRequest) = {
    if (descriptor.heuristics.nonEmpty) {
      // in case we know how to get to the target document without the need to click on the page
      descriptor.heuristics.foreach(h => {
        val paramContainer = row.getFirstByXPath[DomNode](h.parameterPath).getTextContent()
        val pattern = Pattern.compile(h.parameterRegex)
        val m = pattern.matcher(paramContainer)
        if (m.find()) {
          val param = m.group(1)
          val request = new WebRequest(new URL(h.documentDownloadUrl.replace("{param}", param)))
          request.setHttpMethod(HttpMethod.valueOf(h.method))
          if (h.method.equals("POST")) {
            request.setRequestBody(h.bodyParams.get.replace("{param}", param))
            request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded")
          }
          val documentPage: Page = htmlPage.getEnclosingWindow().getWebClient().getPage(request)
          populateDocumentWithDownloadedContent(doc, documentPage, ctx)
          documentPage.getEnclosingWindow().getHistory().back()
        }
      })
    } else {
      val element:HtmlElement = row.getFirstByXPath(descriptor.paths.documentLinkPath.get).asInstanceOf[HtmlElement];
        if (element.hasAttribute("href") && !element.getAttribute("href").equals("#") && !element.getAttribute("href").contains("javascript")) {
    		doc.url = Utilities.getFullUrl(ctx, element);
	    } else { // in case the document is not linked, but a click on a button is required for downloading, get the bytes of the response
	      val link = row.getFirstByXPath[HtmlElement](descriptor.paths.documentLinkPath.get)
		  if (link != null) {
		    val commandString = link.getOnClickAttribute().replaceAll("return ", "")
		    val executeJavaScript = htmlPage.executeJavaScript(commandString)
	        val documentPage = executeJavaScript.getNewPage()
	        populateDocumentWithDownloadedContent(doc, documentPage, ctx)
		    htmlPage = client.getPage(request) // needed, due to a possible bug in htmlunit.
		  }
       }
	}
  }
  
  private def populateDocumentWithDownloadedContent(doc: Document, documentPage: Page, ctx: ExtractionContext) = {
    try {
      val bytes = IOUtils.toByteArray(documentPage.getWebResponse().getContentAsStream())
      doc.content = documentExtractor.extractContent(bytes, ctx)
      doc.url = documentPage.getUrl().toString()
      documentPage.cleanUp()
    } catch {
      case e: Exception => logger.error("Problem extracting document content", e)
    }
  }

  private def buildHtmlClient(): WebClient = {
    val bvf: Array[BrowserVersionFeatures] = new Array[BrowserVersionFeatures](1)
    bvf(0) = BrowserVersionFeatures.HTMLIFRAME_IGNORE_SELFCLOSING
    val bv: BrowserVersion = new BrowserVersion(BrowserVersion.FIREFOX_24.getApplicationName(),
      BrowserVersion.FIREFOX_24.getApplicationVersion(),
      BrowserVersion.FIREFOX_24.getUserAgent(),
      BrowserVersion.FIREFOX_24.getBrowserVersionNumeric(),
      bvf)
    val client: WebClient = new WebClient(bv)
    client.getOptions().setJavaScriptEnabled(descriptor.javascriptRequired.getOrElse(false))
    client.getOptions.setTimeout(120 * 1000)
    client.getOptions().setPrintContentOnFailingStatusCode(false)
    client
  }
}