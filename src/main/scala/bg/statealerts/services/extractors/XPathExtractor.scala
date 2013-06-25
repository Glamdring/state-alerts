package bg.statealerts.services.extractors

import bg.statealerts.services.InformationExtractor
import org.joda.time.DateTime
import javax.xml.xpath.XPathFactory
import bg.statealerts.model.Document
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

class XPathExtractor(contentPath:String, titlePath:String, datePath:String, dateFormat: String) extends InformationExtractor {
  var dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
  
  def extract(since: DateTime): List[Document] = {
	  val factory = XPathFactory.newInstance()
	  val xpath = factory.newXPath()
	  val contentField = xpath.compile(contentPath)
	  val titleField = xpath.compile(titlePath)
	  val dateField = xpath.compile(datePath)
	  val result = List[Document]()
	  
	  while (true) {
	    val doc = new Document()
	    val docContent = ""
	    doc.content = contentField.evaluate(docContent)
	    doc.title = titleField.evaluate(docContent)
	    doc.publishDate = dateTimeFormatter.parseDateTime(dateField.evaluate(docContent));
	  }
	  result;
	}
}