package bg.statealerts.services

import java.io.File

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.NumericRangeQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.SearcherFactory
import org.apache.lucene.search.SearcherManager
import org.apache.lucene.search.Sort
import org.apache.lucene.search.SortField
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import bg.statealerts.dao.DocumentDao
import bg.statealerts.model.Document
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

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
  
  var searcherManager: SearcherManager = _
  
  @PostConstruct
  def init() = {
    searcherManager = new SearcherManager(FSDirectory.open(new File(indexPath)), new SearcherFactory())
    analyzer = Class.forName(analyzerClass).getConstructor(classOf[Version]).newInstance(Version.LUCENE_43).asInstanceOf[Analyzer]
  }

  @PreDestroy
  def destroy() = {
    searcherManager.close()
  }
  
  @Transactional(readOnly=true)
  def search(keywords: String): List[Document] = {
    val escapedKeywords = QueryParserUtil.escape(keywords)
    val q = new TermQuery(new Term("text", escapedKeywords))
    
    getDocuments(q, 50)
  }
  
  @Transactional(readOnly=true)
  def search(keywords: String, since: DateTime): List[Document] = {
    val sinceMillis = since.getMillis()
    val nowMillis = System.currentTimeMillis()
    
    val escapedKeywords = QueryParserUtil.escape(keywords)
    val textQuery = new TermQuery(new Term("text", escapedKeywords))
    val timestampQuery = NumericRangeQuery.newLongRange("indexTimestamp", sinceMillis, nowMillis, true, true)
    val query = new BooleanQuery()
    query.add(textQuery, BooleanClause.Occur.MUST)
    query.add(timestampQuery, BooleanClause.Occur.MUST)
    
    getDocuments(query, 50)
  }
  
  private def getDocuments(query: Query, limit: Int): List[Document] = {
    val searcher = searcherManager.acquire()
    
    try {
	    val sort = new Sort(new SortField("publishTimestamp", SortField.Type.LONG, true))
	    val result: TopDocs = searcher.search(query, null, limit, sort)
	
	    var documents = List[Document]()
	
	    val topDocs: Array[ScoreDoc] = result.scoreDocs
	    
	    for (topDoc <- topDocs) {
	      val luceneDoc = searcher.doc(topDoc.doc)
	      val doc = documentDao.get(classOf[Document], luceneDoc.get("id").toInt)
	      documents ::= doc
	    }
	    documents
    } finally {
      searcherManager.release(searcher)
    }
  }
  
  @Scheduled(fixedRate = 600000) // 10 minutes
  def refreshSearchers() = {
    searcherManager.maybeRefresh()
  }
}