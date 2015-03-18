package bg.statealerts.scraper

import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlElement
import bg.statealerts.scraper.model.Document
import bg.statealerts.scraper.config.ContentLocationType

class TableContentExtractor extends DocumentDetailsExtractor {

  def populateDocument(doc: Document, row: HtmlElement, rowIdx: Int, ctx: ExtractionContext) = {

    // if there is more than one element that matches the XPath, use the rowIdx (some tables do not have proper row separation, so the idx may be needed)
    ctx.descriptor.paths.datePath.foreach (p => {
      val elements = row.getByXPath(p)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find date element for xpath " + p)
      }
      val element = if (elements.size() > 1 && ctx.descriptor.entriesPerRow.nonEmpty) elements.get(rowIdx) else elements.get(0)
      var dateText = element.asInstanceOf[DomNode].getTextContent().trim()
      ctx.descriptor.dateRegex.foreach(regex => {  
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(dateText)
        if (matcher.find()) {
          dateText = matcher.group().trim()
        }
      })
      if (StringUtils.isNotBlank(dateText)) { 
    	  doc.publishDate = ctx.dateFormatter.parseDateTime(dateText).withTimeAtStartOfDay() 
      }
    })

    ctx.descriptor.paths.titlePath.foreach (p => {
      val elements = row.getByXPath(p)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find title element for xpath " + p)
      }
      val element = if (elements.size() > 1 && ctx.descriptor.entriesPerRow.nonEmpty) elements.get(rowIdx) else elements.get(0)
      doc.title = element.asInstanceOf[DomNode].getTextContent().trim()
    })
    
    ctx.descriptor.paths.externalIdPath.foreach (p => {
      val elements = row.getByXPath(p)
      if (elements.size() == 0) {
        throw new IllegalStateException("Cannot find externalId element for xpath " + p)
      }
      val element = if (elements.size() > 1 && ctx.descriptor.entriesPerRow.nonEmpty) elements.get(rowIdx) else elements.get(0)
      doc.externalId = element.asInstanceOf[DomNode].getTextContent().trim()
    })
    
    if (ctx.descriptor.paths.additionalMetaDataPaths.nonEmpty) {
        val metaData = scala.collection.mutable.Map[String, String]()
        for (path <- ctx.descriptor.paths.additionalMetaDataPaths.get) {
          val value = row.getFirstByXPath(path._2);
          if (value != null) {
          	metaData.put(path._1, value)
          }
        }
        doc.additionalMetaData = metaData.toMap;
    }
    //TODO use rowIdx
    if (ContentLocationType.withName(ctx.descriptor.contentLocationType) == ContentLocationType.Table && ctx.descriptor.paths.contentPath.nonEmpty) {
    	doc.content = row.getFirstByXPath(ctx.descriptor.paths.contentPath.get).asInstanceOf[DomNode].getTextContent().trim()
    }
  }
}