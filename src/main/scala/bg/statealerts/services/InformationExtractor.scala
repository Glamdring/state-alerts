package bg.statealerts.services

import org.joda.time.DateTime
import bg.statealerts.model.Document

/**
 * Content may be contained:
 * in the table
 * on a page linked from the table
 * in a document linked on a page linked from the table
 * in a document linked (or inferrable) from the table
 */
trait InformationExtractor {
  def extract(since: DateTime): List[Document];
}