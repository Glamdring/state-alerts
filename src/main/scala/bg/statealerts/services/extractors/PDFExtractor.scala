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
    val result = List[Document]()
    val loop = new Breaks();
    val client: WebClient = new WebClient()
    
    loop.breakable {
      while (true) {
        val request: WebRequestSettings = new WebRequestSettings(new URL(pager.getNextPageUrl()), HttpMethod.valueOf(httpMethod));
        val htmlPage: HtmlPage = client.getPage(request);
      	val publishDateList: List[HtmlElement] = htmlPage.getByXPath(datePath).asInstanceOf[List[HtmlElement]]
		val linkList: List[HtmlElement] = htmlPage.getByXPath(documentLinkPath).asInstanceOf[List[HtmlElement]];
      	val linkIterator: Iterator[HtmlElement] = linkList.iterator
        try {
          for (element <- publishDateList) {
            val doc = new Document()
            doc.publishDate = dateTimeFormatter.parseDateTime(element.getTextContent())

            if (doc.publishDate.isBefore(since)) {
              loop.break;
            }
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
            result :+ doc
          }
        } catch {
          case ex: XPathExpressionException =>
        }
      }
    }

    null
  }
}