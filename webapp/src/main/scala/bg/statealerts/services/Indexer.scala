package bg.statealerts.services

import java.io.File

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.{Document => LuceneDocument}
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.LongField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

import bg.statealerts.dao.DocumentDao
import bg.statealerts.model.Document
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.Entity

@Service
class Indexer {

  @Value("${index.path}")
  var indexPath: String = _

  @Value("${lucene.analyzer.class}")
  var analyzerClass: String = _
  
  @Inject
  var documentDao: DocumentDao = _
  
  @PostConstruct
  def init() = {
    val writer = getWriter()
    writer.commit()
    writer.close()
  }

  private def getWriter() = {
    val dir: Directory = FSDirectory.open(new File(indexPath))
    val analyzer: Analyzer = Class.forName(analyzerClass).getConstructor(classOf[Version]).newInstance(Version.LUCENE_43).asInstanceOf[Analyzer]
    val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, analyzer)
    config.setOpenMode(OpenMode.CREATE_OR_APPEND)
    val writer = new IndexWriter(dir, config)
    writer
  }
  
  def index(documents: List[Document]) = {
    val writer = getWriter()
    val now = new DateTime()
    for (document <- documents) {
	    writer.addDocument(getLuceneDocument(document, now))
    }
    writer.commit()
    writer.close()
  }

  def reindex() = {
    val writer = getWriter()
    writer.deleteAll()
    val now = new DateTime()
    documentDao.performBatched(
      document => writer.addDocument(getLuceneDocument(document, now))
    )
    writer.commit()
    writer.close()
  }
  
  def getLuceneDocument(document: Document, time: DateTime) = {
	  val luceneDoc = new LuceneDocument()
	  luceneDoc.add(new LongField("id", document.id, Store.YES))
	  luceneDoc.add(new LongField("publishTimestamp", document.publishDate.getMillis(), Store.NO))
	  luceneDoc.add(new LongField("indexTimestamp", time.getMillis(), Store.YES))
	  luceneDoc.add(new TextField("sourceKey", document.getSourceKey, Store.YES))
	  luceneDoc.add(new TextField("text", document.content, Store.YES))
	  luceneDoc.add(new TextField("externalId", document.externalId, Store.YES))
	  luceneDoc.add(new TextField("title", document.title, Store.YES))
	  luceneDoc
  }
}