package bg.statealerts.services.extractors

import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.model.Document
import java.util.regex.Pattern
import com.gargoylesoftware.htmlunit.html.DomNode
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateMidnight
import org.joda.time.DateTimeZone

class TableContentExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, rowIdx: Int, ctx: ExtractionContext) = {

    // if there is more than one element that matches the XPath, use the rowIdx (some tables do not have proper row separation, so the idx may be needed)
    if (ctx.descriptor.datePath.nonEmpty) {
      val elements = row.getByXPath(ctx.descriptor.datePath.get)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find date element for xpath " + ctx.descriptor.datePath)
      }
      val element = if (elements.size() > 1 && ctx.descriptor.entriesPerRow.nonEmpty) elements.get(rowIdx) else elements.get(0)
      var text = element.asInstanceOf[DomNode].getTextContent().trim()
      ctx.descriptor.dateRegex.foreach(regex => {  
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
          text = matcher.group().trim()
        }
      })
      if (StringUtils.isNotBlank(text)) { 
    	  doc.publishDate = new DateMidnight(ctx.dateFormatter.parseDateTime(text)).toDateTime()
      }
    }

    if (ctx.descriptor.titlePath.nonEmpty) {
      val elements = row.getByXPath(ctx.descriptor.titlePath.get)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find title element for xpath " + ctx.descriptor.titlePath)
      }
      val element = if (elements.size() > 1 && ctx.descriptor.entriesPerRow.nonEmpty) elements.get(rowIdx) else elements.get(0)
      doc.title = element.asInstanceOf[DomNode].getTextContent().trim()
    }
    if (ctx.descriptor.externalIdPath.nonEmpty) {
      val elements = row.getByXPath(ctx.descriptor.externalIdPath.get)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find externalId element for xpath " + ctx.descriptor.externalIdPath)
      }
      val element = if (elements.size() > 1 && ctx.descriptor.entriesPerRow.nonEmpty) elements.get(rowIdx) else elements.get(0)
      doc.externalId = element.asInstanceOf[DomNode].getTextContent().trim()
    }
    //TODO use rowIdx
    if (ctx.descriptor.contentLocationType == ContentLocationType.Table && ctx.descriptor.contentPath.nonEmpty) {
    	doc.content = row.getFirstByXPath(ctx.descriptor.contentPath.get).asInstanceOf[DomNode].getTextContent().trim()
    }
  }
}