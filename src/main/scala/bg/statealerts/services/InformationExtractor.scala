package bg.statealerts.services

import org.joda.time.DateTime
import bg.statealerts.model.Document

trait InformationExtractor {
  def extract(since: DateTime): List[Document];
}