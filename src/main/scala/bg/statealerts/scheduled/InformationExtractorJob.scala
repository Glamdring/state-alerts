package bg.statealerts.scheduled

import java.io.File

import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import bg.statealerts.services.InformationExtractor
import javax.annotation.PostConstruct

@Component
class InformationExtractorJob {

  var extractors: List[InformationExtractor] = List()
  val mapper: ObjectMapper = new ObjectMapper()
  
  @Value("${config.location}")
  var configLocation:String = _
  
  @PostConstruct
  def init() {
    var file:File = new File(configLocation + "/extractors.xml")
    var config = mapper.readValue(file, classOf[ExtractorConfiguration]);
    for (descriptor <- config.extractors) {
      var extractor = Class.forName("bg.statealerts.services.extractors." + descriptor.extractorType + "Extractor").newInstance();
      extractor :: extractors;
    }
  }
  
  @Scheduled(fixedRate=100000)
  def run() {
    for (extractor <- extractors) {
    }
  }
}