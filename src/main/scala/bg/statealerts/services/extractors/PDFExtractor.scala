package bg.statealerts.services.extractors

import org.apache.pdfbox.pdmodel.PDDocument
import org.joda.time.DateTime
import bg.statealerts.model.Document
import bg.statealerts.services.InformationExtractor
import java.net.URL
import javax.xml.xpath.XPathFactory
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

class PDFExtractor(url: String, paginationUrl: String, datePath: String, dateFormat: String, documentLinkPath: String) extends InformationExtractor {
	var dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
	
  def extract(since: DateTime):List[Document] = {
     val factory = XPathFactory.newInstance()
	 val xpath = factory.newXPath()
	 val documentField = xpath.compile(documentLinkPath)
	 val dateField = xpath.compile(datePath)
	 val result = List[Document]()
	  
	 while (true) {
	   val doc = new Document()
	   val docContent = ""
	   doc.publishDate = dateTimeFormatter.parseDateTime(dateField.evaluate(docContent));
	   val pdfDoc: PDDocument = PDDocument.load(new URL(url));
	 }
	  
    null
  }
}