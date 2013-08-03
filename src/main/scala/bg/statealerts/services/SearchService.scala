package bg.statealerts.services

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import bg.statealerts.model.Document
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import org.joda.time.DateTime
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil

@Service
@DependsOn(Array("indexer")) // indexer initializes index
class SearchService {

  val analyzer: Analyzer = new StandardAnalyzer(Version.LUCENE_43)

  @Value("${index.path}")
  var indexPath: String = _
  var indexReader: IndexReader = _
  var searcher: IndexSearcher = _

  @PostConstruct
  def init() = {
    indexReader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
    searcher = new IndexSearcher(indexReader);
  }

  @PreDestroy
  def destroy() = {
    indexReader.close()
  }
  def search(keywords: String): List[Document] = {
    val escapedKeywords = QueryParserUtil.escape(keywords)
    val parser: QueryParser = new QueryParser(Version.LUCENE_43, "text", analyzer);
    val query: Query = parser.parse(escapedKeywords)
    val result: TopDocs = searcher.search(query, 20)

    var documents = List[Document]()

    val topDocs: Array[ScoreDoc] = result.scoreDocs
    
    for (topDoc <- topDocs) {
      val luceneDoc = searcher.doc(topDoc.doc)
      val doc = getDocument(luceneDoc)
      documents ::= doc
    }
    documents
  }
  
  private def getDocument(luceneDoc: org.apache.lucene.document.Document): bg.statealerts.model.Document = {
    val doc = new Document()
    doc.id = luceneDoc.get("id").toInt
    doc.publishDate = new DateTime(luceneDoc.get("timestamp").toLong)
    doc.title = luceneDoc.get("title")
    doc.content = luceneDoc.get("text")
    doc
  }
}