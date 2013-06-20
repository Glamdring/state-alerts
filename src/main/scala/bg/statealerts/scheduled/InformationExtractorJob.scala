package bg.statealerts.scheduled

import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import bg.statealerts.services.InformationExtractor
import javax.inject.Inject

@Component
class InformationExtractorJob {

  @Inject
  var extractors: List[InformationExtractor] = List();
  
  @Scheduled(fixedRate=100000)
  def run() {
    for (extractor <- extractors) {
      extractor.extract();
    }
  }
}