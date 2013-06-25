package bg.statealerts.scheduled

import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import bg.statealerts.services.InformationExtractor
import javax.inject.Inject
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.codehaus.jackson.map.ObjectMapper

@Component
class InformationExtractorJob {

  var extractors: List[InformationExtractor] = List()
  val mapper: ObjectMapper = new ObjectMapper()
  
  @Value("${config.location}")
  var configLocation:String
  @PostConstruct
  def init() {
  }
  
  @Scheduled(fixedRate=100000)
  def run() {
    for (extractor <- extractors) {
    }
  }
}