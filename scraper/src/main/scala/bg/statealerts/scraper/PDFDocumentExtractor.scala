package bg.statealerts.scraper

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
import java.io.{IOException, InputStream}
import java.net.URL
import java.io.ByteArrayInputStream

class PDFDocumentExtractor extends DocumentFileExtractor {

  def extractContent(documentUrl: String, ctx: ExtractionContext): String = {
    var pdfDoc: PDDocument = null
    var in: InputStream = null
    try {
      in = new URL(documentUrl).openStream()
      pdfDoc = PDDocument.load(in)
      val extractor = new PDFTextStripper("utf-8") //not thread-safe, so a new instance for each document
      return extractor.getText(pdfDoc)
    } catch {
      case e: IOException => {
        e.printStackTrace()
        return ""
      }
    } finally {
      if (pdfDoc != null) pdfDoc.close()
      if (in != null) in.close()
    }
  }
  
  def extractContent(bytes: Array[Byte], ctx: ExtractionContext): String = {
    var pdfDoc: PDDocument = null
    try {
      pdfDoc = PDDocument.load(new ByteArrayInputStream(bytes))
      val extractor = new PDFTextStripper("utf-8") //not thread-safe, so a new instance for each document
      return extractor.getText(pdfDoc)
    } catch {
      case e: IOException => {
        e.printStackTrace()
        return ""
      }
    } finally {
      if (pdfDoc != null) pdfDoc.close()
    }
  }
}