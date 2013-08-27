package bg.statealerts.scraper.model

import scala.beans.BeanProperty
import org.joda.time.DateTime

case class Document {

  @BeanProperty
  var title: String = _
  
  @BeanProperty
  var content: String = _
  
  @BeanProperty
  var publishDate: DateTime = _
  
  @BeanProperty
  var sourceKey: String = _
  
  @BeanProperty
  var sourceDisplayName: String = _
  
  @BeanProperty
  var url: String = _
  
  @BeanProperty
  var externalId: String = _
  
  var importTime:DateTime = _
}