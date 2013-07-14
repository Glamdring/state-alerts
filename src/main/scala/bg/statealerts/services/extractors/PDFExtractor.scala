package bg.statealerts.services.extractors

import java.net.URL
import scala.util.control.Breaks
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequestSettings
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import bg.statealerts.model.Document
import bg.statealerts.services.InformationExtractor
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory
import java.util.ArrayList
import scala.collection.JavaConversions._
import scala.collection.mutable.Buffer

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
    val client: WebClient = new WebClient()
    client.setJavaScriptEnabled(false)

    loop.breakable {
      while (true) {
        val request: WebRequestSettings = new WebRequestSettings(new URL(pager.getNextPageUrl()), HttpMethod.valueOf(httpMethod));
        val htmlPage: HtmlPage = client.getPage(request);
        val list = htmlPage.getByXPath(datePath).asInstanceOf[ArrayList[HtmlElement]]
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