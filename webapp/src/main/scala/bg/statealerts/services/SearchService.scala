package bg.statealerts.services

import java.io.File

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.ReaderManager
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.NumericRangeQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.Sort
import org.apache.lucene.search.SortField
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.joda.time.Interval
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

  var readerManager: ReaderManager = _

  @PostConstruct
  def init() = {
    readerManager = new ReaderManager(FSDirectory.open(new File(indexPath)))
    analyzer = Class.forName(analyzerClass).getConstructor(classOf[Version]).newInstance(Version.LUCENE_43).asInstanceOf[Analyzer]
  }

  @PreDestroy
  def destroy() = {
    readerManager.close()
  }

  @Transactional(readOnly=true)
  def search(keywords: String): Seq[Document] = {
    val q = getTextQuery(keywords)

    getDocuments(q, 50)
  }

  @Transactional(readOnly=true)
  def search(keywords: String, interval: Interval, sources: Seq[String]): Seq[Document] = {

    val textQuery = getTextQuery(keywords)
    val timestampQuery = NumericRangeQuery.newLongRange("indexTimestamp", interval.getStartMillis(), interval.getEndMillis(), true, true)
    val query = new BooleanQuery()
    query.add(textQuery, BooleanClause.Occur.MUST)
    query.add(timestampQuery, BooleanClause.Occur.MUST)
    if (!sources.isEmpty)
        query.add(new TermQuery(new Term("sourceKey", "(" + sources.mkString(" OR ") + ")")), BooleanClause.Occur.MUST)

    getDocuments(query, 50)
  }

  private def getTextQuery(keywords: String): BooleanQuery = {
    val escapedKeywords = QueryParserUtil.escape(keywords).toLowerCase()
    val keywordsList = escapedKeywords.split(" ")
    val query = new BooleanQuery()
    keywordsList.foreach(keyword => {
      query.add(new TermQuery(new Term("text", keyword)), BooleanClause.Occur.MUST)
    })
    return query
  } 
  private def getDocuments(query: Query, limit: Int): Seq[Document] = {
    val reader = readerManager.acquire()

    try {
        val searcher = new IndexSearcher(reader)
        val sort = new Sort(new SortField("publishTimestamp", SortField.Type.LONG, true))
        val result: TopDocs = searcher.search(query, null, limit, sort)

        val topDocs: Array[ScoreDoc] = result.scoreDocs
        var ids = List[Int]()
        for (topDoc <- topDocs) {
          val luceneDoc = searcher.doc(topDoc.doc)
          ids ::= luceneDoc.get("id").toInt
        }
        val documents = documentDao.getDocuments(ids) 
        documents
    } finally {
      readerManager.release(reader)
    }
  }

  @Scheduled(fixedRateString = "${index.refreshRate}")
  def refreshReaderManager(): Unit = {
    readerManager.maybeRefresh()
  }
}
