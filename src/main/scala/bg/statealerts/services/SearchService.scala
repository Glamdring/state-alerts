package bg.statealerts.services

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.Sort
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import bg.statealerts.model.Document
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import org.apache.lucene.search.SortField
import bg.statealerts.dao.DocumentDao
import javax.inject.Inject
import org.springframework.transaction.annotation.Transactional

@Service
@DependsOn(Array("indexer")) // indexer initializes index
class SearchService {

  var analyzer: Analyzer = _

  @Inject
  var documentDao: DocumentDao = _
  
  @Value("${index.path}")
  var indexPath: String = _
  @Value("${lucene.analyzer.class}")
  var analyzerClass: String = _
  
  var indexReader: IndexReader = _
  var searcher: IndexSearcher = _

  @PostConstruct
  def init() = {
    indexReader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
    searcher = new IndexSearcher(indexReader);
    analyzer = Class.forName(analyzerClass).getConstructor(classOf[Version]).newInstance(Version.LUCENE_43).asInstanceOf[Analyzer]
  }

  @PreDestroy
  def destroy() = {
    indexReader.close()
  }
  
  @Transactional(readOnly=true)
  def search(keywords: String): List[Document] = {
    val escapedKeywords = QueryParserUtil.escape(keywords)
    val q = new TermQuery(new Term("text", escapedKeywords + "*"))
    val sort = new Sort(new SortField("timestamp", SortField.Type.LONG, true))
    val result: TopDocs = searcher.search(q, null, 50, sort)

    var documents = List[Document]()

    val topDocs: Array[ScoreDoc] = result.scoreDocs
    
    for (topDoc <- topDocs) {
      val luceneDoc = searcher.doc(topDoc.doc)
      val doc = documentDao.get(classOf[Document], luceneDoc.get("id").toInt)
      documents ::= doc
    }
    documents
  }
}