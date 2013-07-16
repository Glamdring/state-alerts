package bg.statealerts.services.extractors

import scala.collection.JavaConversions.asScalaBuffer
import scala.util.control.Breaks
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures
import bg.statealerts.model.Document
import bg.statealerts.services.InformationExtractor
import javax.xml.xpath.XPathFactory
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import java.net.URL
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.html.HtmlPage
import java.util.ArrayList
import com.gargoylesoftware.htmlunit.html.HtmlElement
import scala.collection.mutable.Buffer
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
import org.apache.commons.io.IOUtils
import java.io.InputStream

class PDFExtractor(url: String,
  httpMethod: String,
  datePath: String,
  dateFormat: String,
  documentLinkPath: String,
  documentPageLinkPath: Option[String],
  pagingMultiplier: Int) extends InformationExtractor {

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
  val pager: Pager = new Pager(url, pagingMultiplier);
  val baseUrl: String = {
    val fullUrl = new URL(url);
    fullUrl.getProtocol() + "://" + fullUrl.getHost() + ":" + fullUrl.getPort();
  }

  def extract(since: DateTime): List[Document] = {
    val factory = XPathFactory.newInstance()
    val xpath = factory.newXPath()
    val documentField = xpath.compile(documentLinkPath)
    val dateField = xpath.compile(datePath)
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
          val list = htmlPage.getByXPath(datePath).asInstanceOf[ArrayList[HtmlElement]]
          if (list.isEmpty()) {
            loop.break
          }

          val publishDateList: Buffer[HtmlElement] = asScalaBuffer(list)
          val linkJavaList = htmlPage.getByXPath(documentLinkPath).asInstanceOf[ArrayList[HtmlElement]]
          val linkList: Buffer[HtmlElement] = asScalaBuffer(linkJavaList);

          if (linkList.size != publishDateList.size) {
            throw new IllegalStateException("Document links are fewer than dates. Check your XPath");
          }

          val linkIterator: Iterator[HtmlElement] = linkList.iterator
          for (element <- publishDateList) {
            val doc = new Document()
            doc.publishDate = dateTimeFormatter.parseDateTime(element.getTextContent())

            if (doc.publishDate.isBefore(since)) {
              loop.break;
            }

            var documentUrl: String = linkIterator.next.getAttribute("href");
            if (!documentUrl.startsWith("http")) {
              documentUrl = baseUrl + documentUrl
            }

            // if the link is not available in the table, but on a separate page, go to that page first
            if (documentPageLinkPath.exists(a => true)) {
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
              var pdfDoc: PDDocument = null
              var in: InputStream = null
              try {
            	in = new URL(documentUrl).openStream();
                pdfDoc = PDDocument.load(in);
                var extractor: PDFTextStripper = new PDFTextStripper(); //not thread-safe, so a new instance for each document
                doc.content = extractor.getText(pdfDoc);
              } finally {
                if (pdfDoc != null) pdfDoc.close()
                if (in != null) in.close()
              }
              result ::= doc
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