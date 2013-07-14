package bg.statealerts.services.extractors

import bg.statealerts.services.InformationExtractor
import org.joda.time.DateTime
import javax.xml.xpath.XPathFactory
import bg.statealerts.model.Document
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import java.net.URL
import org.apache.commons.io.IOUtils
import scala.util.control.Breaks
import javax.xml.xpath.XPathExpressionException
import org.springframework.http.HttpMethod
import java.net.HttpURLConnection
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants

class XPathExtractor(url: String,
  httpMethod: String,
  contentPath: String,
  titlePath: String,
  datePath: String,
  dateFormat: String,
  pagingMultiplier: Int) extends InformationExtractor {
  var dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
  val pager: Pager = new Pager(url, pagingMultiplier);

  def extract(since: DateTime): List[Document] = {
    val factory = XPathFactory.newInstance()
    val xpath = factory.newXPath()
    val contentField = xpath.compile(contentPath)
    val titleField = xpath.compile(titlePath)
    val dateField = xpath.compile(datePath)
    var result = List[Document]()
    val loop = new Breaks();
    loop.breakable {
      while (true) {
        val conn = new URL(pager.getNextPageUrl()).openConnection().asInstanceOf[HttpURLConnection]
        conn.setRequestMethod(httpMethod)
        val in = conn.getInputStream()
        val page = IOUtils.toString(in)

        try {
          val publishDateList: NodeList = dateField.evaluate(page, XPathConstants.NODESET).asInstanceOf[NodeList];
          val titleList: NodeList = titleField.evaluate(page, XPathConstants.NODESET).asInstanceOf[NodeList];
          val contentList: NodeList = contentField.evaluate(page, XPathConstants.NODESET).asInstanceOf[NodeList];

          for (i <- 0 until publishDateList.getLength()) {
            val doc = new Document()
            doc.publishDate = dateTimeFormatter.parseDateTime(publishDateList.item(i).getTextContent())
            if (doc.publishDate.isAfter(since)) {
              loop.break
            }
            doc.content = contentList.item(i).getTextContent()
            doc.title = titleList.item(i).getTextContent()
            result ::= doc
          }
        } catch {
          case ex: XPathExpressionException =>
        }
      }
    }
    result;
  }
}