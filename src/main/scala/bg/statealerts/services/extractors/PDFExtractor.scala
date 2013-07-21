package bg.statealerts.services.extractors

import java.io.InputStream
import java.net.URL
import java.util.ArrayList
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.Buffer
import scala.util.control.Breaks
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
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
import bg.statealerts.services.InformationExtractor
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathExpression
import bg.statealerts.scheduled.ContentLocationType

class PDFExtractor(descriptor: ExtractorDescriptor) extends InformationExtractor {

  val url: String = descriptor.url
  val httpMethod: String = descriptor.httpMethod
  val datePath: Option[String] = descriptor.datePath
  val documentPageDatePath: Option[String] = descriptor.documentPageDatePath
  val dateFormat: String = descriptor.dateFormat
  val titlePath: Option[String] = descriptor.titlePath
  val documenetPageTitlePath: Option[String] = descriptor.documentPageTitlePath
  val documentLinkPath: Option[String] = descriptor.documentLinkPath
  val documentPageLinkPath: Option[String] = descriptor.documentPageLinkPath
  val contentPath: Option[String] = descriptor.contentPath
  val pagingMultiplier: Int = descriptor.pagingMultiplier
  val flowType: ContentLocationType.Value = descriptor.flowType
  val tableRowPath = descriptor.tableRowPath

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat)

  val pager: Pager = new Pager(url, pagingMultiplier)
  val baseUrl: String = {
    val fullUrl = new URL(url)
    var port = fullUrl.getPort()
    if (port == -1) {
      port = 80
    }
    fullUrl.getProtocol() + "://" + fullUrl.getHost() + ":" + port
  }

  def extract(since: DateTime): List[Document] = {
    var result = List[Document]()
    val loop = new Breaks();

    val bvf: Array[BrowserVersionFeatures] = new Array[BrowserVersionFeatures](1)
    bvf(0) = BrowserVersionFeatures.HTMLIFRAME_IGNORE_SELFCLOSING
    val bv: BrowserVersion = new BrowserVersion(BrowserVersion.FIREFOX_17.getApplicationName(),
      BrowserVersion.FIREFOX_17.getApplicationVersion(),
      BrowserVersion.FIREFOX_17.getUserAgent(),
      BrowserVersion.FIREFOX_17.getBrowserVersionNumeric(),
      bvf);
    val client: WebClient = new WebClient(bv)

    client.setJavaScriptEnabled(false)

    loop.breakable {
      while (true) {
        // each iteration is in a try/catch, so that one failure doesn't fail the whole batch
        try {
          val pageUrl = pager.getNextPageUrl()
          val request: WebRequest = new WebRequest(new URL(pageUrl), HttpMethod.valueOf(httpMethod));
          // POST parameters are set in the request body
          if (HttpMethod.valueOf(httpMethod) == HttpMethod.POST) {
            val body = pageUrl.substring(pageUrl.indexOf('?') + 1)
            request.setRequestBody(body)
            request.setUrl(new URL(pageUrl.replace("?" + body, "")))
          }

          val htmlPage: HtmlPage = client.getPage(request);
          val list = asScalaBuffer(htmlPage.getByXPath(tableRowPath).asInstanceOf[ArrayList[HtmlElement]])
          if (list.isEmpty) {
            loop.break
          }

          for (row <- list) {
            val doc = new Document()
            doc.sourceName = descriptor.sourceName
            
            val extractor = new TableContentExtractor(); //TODO reuse the same instance
            extractor.populateDocument(doc, row, descriptor, baseUrl, dateTimeFormatter)
            if (doc.publishDate.isBefore(since)) {
              loop.break;
            }
            if (flowType != ContentLocationType.Table) {
	            var documentUrl: String = row.getFirstByXPath(documentLinkPath.get).asInstanceOf[HtmlElement].getAttribute("href");
	            if (!documentUrl.startsWith("http")) {
	              documentUrl = baseUrl + documentUrl
	            }
	
	            // if the link is not available in the table, but on a separate page, go to that page first
	            if (flowType == ContentLocationType.LinkedDocumentOnLinkedPage || flowType == ContentLocationType.LinkedPage) {
	              var docPage: HtmlPage = client.getPage(documentUrl)
	              var link: HtmlElement = docPage.getFirstByXPath(documentPageLinkPath.get);
	              if (link != null) {
	                documentUrl = link.getAttribute("href")
	                if (!documentUrl.startsWith("http")) {
	                  documentUrl = baseUrl + documentUrl
	                }
	              } else {
	                documentUrl = null
	              }
	            }
	
	            if (documentUrl != null) {
	              val documentExtractor = new PDFDocumentExtractor()
	              doc.content = documentExtractor.extractContent(documentUrl)
	              result ::= doc
	            }
            }
          }
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
    client.closeAllWindows();
    result
  }
}