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

class XPathExtractor(url: String, 
    contentPath:String, 
    titlePath:String, 
    datePath:String, 
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
	  val result = List[Document]()
	  
	  while (true) {
	    val doc = new Document()
	    
	    val in = new URL(pager.getNextPageUrl()).openStream()
	    val page:String = IOUtils.toString(in)
	    IOUtils.closeQuietly(in)
	    
	    try {
		    doc.publishDate = dateTimeFormatter.parseDateTime(dateField.evaluate(page))
		    if (doc.publishDate.isAfter(since)) {
		      Breaks.break
		    }
		    doc.content = contentField.evaluate(page)
		    doc.title = titleField.evaluate(page)
		    result :+ doc
	    } catch { 
        	case ex: XPathExpressionException => 
	    }
	  }
	  result;
	}
}