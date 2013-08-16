package bg.statealerts.services

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import bg.statealerts.model.Document
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import org.apache.lucene.document.LongField
import org.apache.lucene.document.TextField
import org.apache.lucene.document.Field.Store
import org.joda.time.DateTime
import org.apache.lucene.analysis.bg.BulgarianAnalyzer

@Service
class Indexer {

  var writer: IndexWriter = _
  @Value("${index.path}")
  var indexPath: String = _

  @Value("${lucene.analyzer.class}")
  var analyzerClass: String = _
  
  @PostConstruct
  def init() = {
    val dir: Directory = FSDirectory.open(new File(indexPath))
    val analyzer: Analyzer = Class.forName(analyzerClass).getConstructor(classOf[Version]).newInstance(Version.LUCENE_43).asInstanceOf[Analyzer]
    val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, analyzer)
    config.setOpenMode(OpenMode.CREATE_OR_APPEND)
    writer = new IndexWriter(dir, config)
  }

  @PreDestroy
  def destroy() = {
    writer.close()
  }
  
  def index(documents: List[Document]) = {
    val now = new DateTime()
    for (document <- documents) {
	    writer.addDocument(getLuceneDocument(document, now))
    }
    writer.commit()
  }
  
  def getLuceneDocument(document: Document, time: DateTime) = {
	  val luceneDoc = new org.apache.lucene.document.Document()
	  luceneDoc.add(new LongField("id", document.id, Store.YES))
	  luceneDoc.add(new LongField("timestamp", time.getMillis(), Store.YES))
	  luceneDoc.add(new TextField("text", document.content, Store.YES))
	  luceneDoc.add(new TextField("externalId", document.externalId, Store.YES))
	  luceneDoc.add(new TextField("title", document.title, Store.YES))
	  luceneDoc
  }
}