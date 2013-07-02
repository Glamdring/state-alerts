package bg.statealerts.services.extractors

import bg.statealerts.services.InformationExtractor
import bg.statealerts.model.Document
import org.joda.time.DateTime

class BillExtractor extends InformationExtractor {

  def extract(since: DateTime):List[Document] = {
    null
  }
}