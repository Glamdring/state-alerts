package bg.statealerts.scheduled

import java.io.File
import java.util.Random
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import com.codahale.jerkson.Json
import bg.statealerts.dao.DocumentDao
import bg.statealerts.model.Document
import bg.statealerts.model.Import
import bg.statealerts.services.DocumentService
import bg.statealerts.services.Indexer
import bg.statealerts.services.extractors.ContentLocationType
import bg.statealerts.services.extractors.Extractor
import javax.annotation.PostConstruct
import javax.inject.Inject
import org.joda.time.DateTimeConstants
import javax.annotation.Resource
import bg.statealerts.util.TestProfile

@Component
@TestProfile.Disabled
class InformationExtractorJob {

  val logger = LoggerFactory.getLogger(classOf[InformationExtractorJob]) 
  
  @Resource(name="extractors")
  var extractors: List[Extractor] = List()

  @Value("${statealerts.config.location}")
  var configLocation: String = _

  @Value("${random.sleep.max.minutes}")
  var randomSleepMaxMinutes: Int = 0

  @Inject
  var service: DocumentService = _

  @Inject
  var dao: DocumentDao = _

  @Inject
  var indexer: Indexer = _

  val random = new Random()

  @Scheduled(fixedRate = DateTimeConstants.MILLIS_PER_HOUR)
  def run() {
    if (randomSleepMaxMinutes > 0) {
      // sleep a random amount of time before running the extraction, so that it is not completely obvious to website admins it's a scraping process
      Thread.sleep(random.nextInt(randomSleepMaxMinutes * 60) * 1000)
    }
    for (extractor <- extractors) {
      try {
        var lastImportTime = dao.getLastImportDate(extractor.descriptor.sourceKey).getOrElse(new DateTime().minusDays(14).withTimeAtStartOfDay()).withZoneRetainFields(DateTimeZone.UTC)
        val now = DateTime.now()
        var documents: List[Document] = extractor.extract(lastImportTime)
        var persistedDocuments = List[Document]()
        var documentCount = documents.size
        for (document <- documents) {
          try {
	          document.title = StringUtils.left(document.title, 2000);
	          document.url = StringUtils.left(document.url, 2000);
	          //remove unneeded whitespaces new lines
	          document.content = document.content.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2")
	          document.importTime = now
	          persistedDocuments ::= service.save(document)
          } catch {
            case ex: Exception => {
              documentCount -= 1
              logger.error("Error saving document", ex)
            }
          }
        }
        //TODO more effort to keep in sync with db
        indexer.index(persistedDocuments)

        if (documentCount > 0) {
          documents = documents.sortBy {_.publishDate.getMillis}.reverse
          val docImport = new Import()
          docImport.importedDocuments = documentCount
          docImport.latestDocumentDate = documents(0).publishDate
          docImport.sourceKey = extractor.descriptor.sourceKey
          docImport.importTime = now
          service.save(docImport)
        }
      } catch {
        case ex: Exception => logger.error("Problem extracting information from source: " + extractor.descriptor.sourceKey, ex)
      }
    }
  }
  
}