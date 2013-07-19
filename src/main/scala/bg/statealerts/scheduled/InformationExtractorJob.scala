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
import bg.statealerts.services.extractors.PDFExtractor
import org.joda.time.DateTime
import bg.statealerts.services.DocumentService
import bg.statealerts.model.Import
import bg.statealerts.services.Indexer

@Component
class InformationExtractorJob {

  var extractors: List[InformationExtractor] = List()

  @Value("${statealerts.config.location}")
  var configLocation: String = _

  @Inject
  var service: DocumentService = _

  @Inject
  var dao: BaseDao = _
  
  @Inject
  var indexer: Indexer = _
  
  @PostConstruct
  def init() {
    var file: File = new File(configLocation + "/extractors.json")
    var config: ExtractorConfiguration = Json.parse[ExtractorConfiguration](file)
    //TODO validate configuration - required/optional fields per type
    for (descriptor <- config.extractors) {
      var extractor: InformationExtractor = null
      descriptor.extractorType match {
        case "XPath" => extractor = getXPathExtractor(descriptor)
        case "PDF" => extractor = getPDFExtractor(descriptor)
      }
      extractors ::= extractor
    }
  }

  @Scheduled(fixedRate = 100000)
  def run() {
    var lastImportTime = dao.getLastImportDate();
    if (lastImportTime == null) {
      lastImportTime = new DateTime().minusDays(14)
    }
    val now = new DateTime();
    var total: Int = 0
    for (extractor <- extractors) {
      val documents: List[Document] = extractor.extract(lastImportTime)
      var persistedDocuments = List[Document]()
      total += documents.size
      for (document <- documents) {
        persistedDocuments ::= service.save(document)
      }
      //TODO more effort to keep in sync with db
      indexer.index(persistedDocuments)
    }
    
    if (total > 0) {
	    val docImport = new Import()
	    docImport.importedDocuments = total;
	    docImport.importTime = now
	    service.save(docImport)
    }
  }

  private def getXPathExtractor(descriptor: ExtractorDescriptor): InformationExtractor = {
    new XPathExtractor(descriptor.url, descriptor.httpMethod, descriptor.contentPath.get, 
        descriptor.titlePath.get, descriptor.datePath.get, 
        descriptor.dateFormat.get, descriptor.pagingMultiplier);
  }
  private def getPDFExtractor(descriptor: ExtractorDescriptor): InformationExtractor = {
    new PDFExtractor(descriptor.url, descriptor.httpMethod, descriptor.datePath.get, 
        descriptor.dateFormat.get, descriptor.titlePath.get, 
        descriptor.documentLinkPath.get, descriptor.documentPageLinkPath, descriptor.pagingMultiplier);
  }
}