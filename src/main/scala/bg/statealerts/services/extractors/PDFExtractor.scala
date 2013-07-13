package bg.statealerts.services.extractors

import java.net.URL
import scala.util.control.Breaks
import org.apache.commons.io.IOUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import bg.statealerts.model.Document
import bg.statealerts.services.InformationExtractor
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathExpressionException

class PDFExtractor(url: String, 
    datePath: String, 
    dateFormat: String, 
    documentLinkPath: String, 
    pagingMultiplier: Int) extends InformationExtractor {
  
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
  val pager: Pager = new Pager(url, pagingMultiplier);
  
  def extract(since: DateTime): List[Document] = {
    val factory = XPathFactory.newInstance()
    val xpath = factory.newXPath()
    val documentField = xpath.compile(documentLinkPath)
    val dateField = xpath.compile(datePath)
    val result = List[Document]()

    while (true) {
      val doc = new Document()
      val in = new URL(pager.getNextPageUrl()).openStream()
      val page:String = IOUtils.toString(in)
      IOUtils.closeQuietly(in)
      try {
    	  val date = dateField.evaluate(page);
    	  doc.publishDate = dateTimeFormatter.parseDateTime(date)
    	  
    	  if (doc.publishDate.isBefore(since)) {
	        Breaks.break
	      }
	      val documentUrl = documentField.evaluate(page)
	      var pdfDoc: PDDocument = null
	      try {
	    	  val pdfDoc: PDDocument = PDDocument.load(documentUrl);
	    	  var extractor: PDFTextStripper = null;
	    	  doc.content = extractor.getText(pdfDoc);
	      } finally {
	    	  if (pdfDoc != null) pdfDoc.close()
	      }
	      result :+ doc
      } catch { 
        case ex: XPathExpressionException => 
      }
    }

    null
  }
}