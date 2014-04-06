package bg.statealerts.scraper.model

import scala.beans.BeanProperty
import org.joda.time.DateTime

case class Document() {

  var title: String = _
  var content: String = _
  var publishDate: DateTime = _
  var sourceKey: String = _
  var sourceDisplayName: String = _
  var url: String = _
  var externalId: String = _
  var importTime: DateTime = _
  var additionalMetaData: Map[String, String] = Map()
  var metaDataUrl: String = _
}