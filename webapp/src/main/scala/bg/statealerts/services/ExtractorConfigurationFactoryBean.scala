package bg.statealerts.services

import org.springframework.beans.factory.FactoryBean
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import org.joda.time.DateTimeZone
import com.codahale.jerkson.Json
import java.io.File
import org.springframework.beans.factory.annotation.Value
import bg.statealerts.scraper.Extractor
import bg.statealerts.scraper.config.ExtractorDescriptor
import bg.statealerts.scraper.config.ExtractorConfiguration
import bg.statealerts.scraper.config.ContentLocationType

@Component("extractors")
class ExtractorConfigurationFactoryBean extends FactoryBean[List[Extractor]]{

  var extractors: List[Extractor] = List()

  @Value("${statealerts.config.location}")
  var configLocation: String = _
  
  @PostConstruct
  def init() {
    DateTimeZone.setDefault(DateTimeZone.UTC)
    val file = new File(configLocation + "/extractors.json")
    val config = Json.parse[ExtractorConfiguration](file)
    for (descriptor <- config.extractors) {
      validateDescriptor(descriptor) // the application fails on startup if a configuration is invalid
      var extractor = new Extractor(descriptor)
      extractors ::= extractor
    }
  }
  
  override def getObject(): List[Extractor] = {
    extractors
  }
  
  override def getObjectType(): Class[List[ExtractorDescriptor]] = {
    return classOf[List[ExtractorDescriptor]]
  }
  
  override def isSingleton(): Boolean = {
    true
  }
  
  private def validateDescriptor(descriptor: ExtractorDescriptor) {
    val contentLocationType = ContentLocationType.withName(descriptor.contentLocationType)
    if (contentLocationType == ContentLocationType.Table && (!descriptor.paths.titlePath.nonEmpty || !descriptor.paths.datePath.nonEmpty)) {
      throw new IllegalStateException("Required extractor configuration parameters are not present for " + descriptor.sourceKey + ". For contentLocationType=Table, 'titlePath' and 'datePath' are required")
    }
    if (contentLocationType == ContentLocationType.LinkedDocumentInTable && !descriptor.paths.documentLinkPath.nonEmpty) {
      throw new IllegalStateException("Required extractor configuration parameters are not present for " + descriptor.sourceKey + ". For contentLocationType=LinkedDocumentInTable, 'documentLinkPath' is required")
    }
    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage && (!descriptor.paths.documentLinkPath.nonEmpty || !descriptor.paths.documentPageLinkPath.nonEmpty)) {
      throw new IllegalStateException("Required extractor configuration parameters are not present for " + descriptor.sourceKey + ". For contentLocationType=LinkedDocumentOnLinkedPage, 'documentLinkPath' and 'documentPageLinkPath' are required")
    }

    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage || contentLocationType == ContentLocationType.LinkedPage) {
      if (!descriptor.paths.titlePath.nonEmpty && !descriptor.paths.documentPageTitlePath.nonEmpty) {
        throw new IllegalStateException("Extractor " + descriptor.sourceKey + " must have either 'titlePath' or 'documentPageTitlePath'")
      }
      if (!descriptor.paths.datePath.nonEmpty && !descriptor.paths.documentPageDatePath.nonEmpty) {
        throw new IllegalStateException("Extractor " + descriptor.sourceKey + " must have either 'datePath' or 'documentPageDatePath'")
      }
    }
  }
}