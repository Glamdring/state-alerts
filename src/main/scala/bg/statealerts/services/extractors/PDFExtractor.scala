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

class PDFExtractor(url: String,
  httpMethod: String,
  datePath: String,
  dateFormat: String,
  documentLinkPath: String,
  documentPageLinkPath: Option[String],
  pagingMultiplier: Int) extends InformationExtractor {

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
  val pager: Pager = new Pager(url, pagingMultiplier);

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
        println("AA: " + htmlPage.getWebResponse().getContentAsString())
        if (list.isEmpty()) {
          loop.break
        }

        val publishDateList: Buffer[HtmlElement] = asScalaBuffer(list)
        val linkJavaList = htmlPage.getByXPath(documentLinkPath).asInstanceOf[ArrayList[HtmlElement]]
        val linkList: Buffer[HtmlElement] = asScalaBuffer(linkJavaList);

        val linkIterator: Iterator[HtmlElement] = linkList.iterator
        for (element <- publishDateList) {
          val doc = new Document()
          doc.publishDate = dateTimeFormatter.parseDateTime(element.getTextContent())

          if (doc.publishDate.isBefore(since)) {
            loop.break;
          }

          // if the link is not available in the table, but on a separate page, go to that page first
          var documentUrl: String = linkIterator.next.getAttribute("href");
          if (documentPageLinkPath.exists(a => true)) {
            var docPage: HtmlPage = client.getPage(documentUrl)
            var link: HtmlElement = docPage.getFirstByXPath(documentPageLinkPath.get);
            documentUrl = link.getAttribute("href")
          }

          var pdfDoc: PDDocument = null
          try {
            val pdfDoc: PDDocument = PDDocument.load(documentUrl);
            var extractor: PDFTextStripper = null;
            doc.content = extractor.getText(pdfDoc);
          } finally {
            if (pdfDoc != null) pdfDoc.close()
          }
          result ::= doc
        }
      }
    }
    client.closeAllWindows();
    result
  }
}