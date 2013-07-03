package bg.statealerts.scheduled

import java.io.File
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import bg.statealerts.services.InformationExtractor
import javax.annotation.PostConstruct
import bg.statealerts.services.extractors.XPathExtractor
import javax.inject.Inject
import bg.statealerts.dao.BaseDao
import bg.statealerts.model.Document

@Component
class InformationExtractorJob {

  var extractors: List[InformationExtractor] = List()
  val mapper: ObjectMapper = new ObjectMapper()
  
  @Value("${config.location}")
  var configLocation: String = _
  
  @Inject
  var dao: BaseDao = _
  
  @PostConstruct
  def init() {
    var file:File = new File(configLocation + "/extractors.xml")
    var config = mapper.readValue(file, classOf[ExtractorConfiguration])
    for (descriptor <- config.extractors) yield {
      var extractor: InformationExtractor = null
      descriptor.extractorType match {
        case "XPath" => extractor = getXPathExtractor(descriptor)
      }
      extractor :: extractors
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