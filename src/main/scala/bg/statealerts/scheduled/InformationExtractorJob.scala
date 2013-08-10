package bg.statealerts.scheduled

import java.io.File
import org.joda.time.{DateTimeConstants, DateTime}
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import com.codahale.jerkson.Json
import bg.statealerts.dao.BaseDao
import bg.statealerts.model.Document
import bg.statealerts.model.Import
import bg.statealerts.services.DocumentService
import bg.statealerts.services.Indexer
import bg.statealerts.services.extractors.Extractor
import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.Random
import bg.statealerts.services.extractors.ContentLocationType
import org.slf4j.LoggerFactory
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateMidnight

@Component
class InformationExtractorJob {

  val logger = LoggerFactory.getLogger(classOf[InformationExtractorJob]) 
  
  var extractors: List[Extractor] = List()

  @Value("${statealerts.config.location}")
  var configLocation: String = _

  @Value("${random.sleep.max.minutes}")
  var randomSleepMaxMinutes: Int = 0

  @Inject
  var service: DocumentService = _

  @Inject
  var dao: BaseDao = _

  @Inject
  var indexer: Indexer = _

  val random = new Random()

  @PostConstruct
  def init() {
    val file = new File(configLocation + "/extractors.json")
    val config = Json.parse[ExtractorConfiguration](file)
    for (descriptor <- config.extractors) {
      validateDescriptor(descriptor) // the application fails on startup if a configuration is invalid
      var extractor = new Extractor(descriptor)
      extractors ::= extractor
    }
  }

  @Scheduled(fixedRate = DateTimeConstants.MILLIS_PER_HOUR)
  def run() {
    if (randomSleepMaxMinutes > 0) {
      // sleep a random amount of time before running the extraction, so that it is not completely obvious to website admins it's a scraping process
      Thread.sleep(random.nextInt(randomSleepMaxMinutes * 60) * 1000)
    }
    for (extractor <- extractors) {
      try {
        var lastImportTime = dao.getLastImportDate(extractor.sourceName).getOrElse(new DateMidnight().minusDays(14));
        val now = new DateTime()
        var documents: List[Document] = extractor.extract(lastImportTime)
        var persistedDocuments = List[Document]()
        var documentCount = documents.size
        for (document <- documents) {
          try {
	          document.title = StringUtils.left(document.title, 2000);
	          document.title = StringUtils.left(document.url, 2000);
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

        if (documents.size > 0) {
          documents = documents.sortBy {_.publishDate.getMillis}.reverse
          val docImport = new Import()
          docImport.importedDocuments = documentCount
          docImport.latestDocumentDate = documents(0).publishDate
          docImport.sourceName = extractor.sourceName
          docImport.importTime = now
          service.save(docImport)
        }
      } catch {
        case ex: Exception => logger.error("Problem extracting information from source: " + extractor.sourceName, ex)
      }
    }
  }

  def validateDescriptor(descriptor: ExtractorDescriptor) {
    val contentLocationType = ContentLocationType.withName(descriptor.contentLocationType)
    if (contentLocationType == ContentLocationType.Table && (!descriptor.titlePath.nonEmpty || !descriptor.datePath.nonEmpty)) {
      throw new IllegalStateException("Required extractor configuration parameters are not present for " + descriptor.sourceName + ". For contentLocationType=Table, 'titlePath' and 'datePath' are required")
    }
    if (contentLocationType == ContentLocationType.LinkedDocumentInTable && !descriptor.documentLinkPath.nonEmpty) {
      throw new IllegalStateException("Required extractor configuration parameters are not present for " + descriptor.sourceName + ". For contentLocationType=LinkedDocumentInTable, 'documentLinkPath' is required")
    }
    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage && (!descriptor.documentLinkPath.nonEmpty || !descriptor.documentPageLinkPath.nonEmpty)) {
      throw new IllegalStateException("Required extractor configuration parameters are not present for " + descriptor.sourceName + ". For contentLocationType=LinkedDocumentOnLinkedPage, 'documentLinkPath' and 'documentPageLinkPath' are required")
    }

    if (contentLocationType == ContentLocationType.LinkedDocumentOnLinkedPage || contentLocationType == ContentLocationType.LinkedPage) {
      if (!descriptor.titlePath.nonEmpty && !descriptor.documentPageTitlePath.nonEmpty) {
        throw new IllegalStateException("Extractor " + descriptor.sourceName + " must have either 'titlePath' or 'documentPageTitlePath'")
      }
      if (!descriptor.datePath.nonEmpty && !descriptor.documentPageDatePath.nonEmpty) {
        throw new IllegalStateException("Extractor " + descriptor.sourceName + " must have either 'datePath' or 'documentPageDatePath'")
      }
    }
  }
}