package bg.statealerts.scheduled

import java.io.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper
import bg.statealerts.dao.BaseDao
import bg.statealerts.model.Document
import bg.statealerts.services.InformationExtractor
import bg.statealerts.services.extractors.XPathExtractor
import javax.annotation.PostConstruct
import javax.inject.Inject
import com.codahale.jerkson.Json

@Component
class InformationExtractorJob {

  var extractors: List[InformationExtractor] = List()
  val mapper: ObjectMapper = {
    var mapper = new ObjectMapper();
    mapper
  }
  
  @Value("${statealerts.config.location}")
  var configLocation: String = _
  
  @Inject
  var dao: BaseDao = _
  
  @PostConstruct
  def init() {
    var file:File = new File(configLocation + "/extractors.json")
    var config:ExtractorConfiguration = Json.parse[ExtractorConfiguration](file)
    for (descriptor <- config.extractors) yield {
      var extractor: InformationExtractor = null
      descriptor.extractorType match {
        case "XPath" => extractor = getXPathExtractor(descriptor)
      }
      extractors = extractor :: extractors
    }
  }
  
  @Scheduled(fixedRate=100000)
  def run() {
    var lastImportTime = dao.getLastImportDate();
    for (extractor <- extractors) {
      var document: List[Document] = extractor.extract(lastImportTime);
    }
  }
  
  private def getXPathExtractor(descriptor: ExtractorDescriptor): InformationExtractor = {
    new XPathExtractor(descriptor.url, descriptor.contentPath, descriptor.titlePath, descriptor.datePath, descriptor.dateFormat);
  }
}