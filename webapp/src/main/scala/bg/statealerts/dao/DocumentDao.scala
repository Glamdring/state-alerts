package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import bg.statealerts.model.Document
import javax.persistence.TypedQuery
import scala.collection.JavaConversions._

@Repository
class DocumentDao extends BaseDao {
  
  @Value("${document.fetch.size:200}")
  var fetchSize: Int = _
  
  def getLastImportDate(sourceKey: String): Option[DateTime] = {
    val query: TypedQuery[DateTime] = entityManager.createQuery("SELECT latestDocumentDate FROM Import WHERE sourceKey = :sourceKey ORDER BY latestDocumentDate DESC", classOf[DateTime])
    query.setMaxResults(1)
    query.setParameter("sourceKey", sourceKey)

    val result = query.getResultList()
    if (result.isEmpty()) {
      return None
    } else {
      return Some(result.get(0).withTimeAtStartOfDay())
    }
  }

  def performBatched(f: Document => Unit): Unit = performBatched(classOf[Document], fetchSize)(f)
  
  def getDocuments(ids: List[Int]): Seq[Document] = {
    if (ids.isEmpty) {
      return List.empty
    }
    val query: TypedQuery[Document] = entityManager.createQuery("SELECT doc FROM Document doc WHERE id IN(:ids)", classOf[Document])
    query.setMaxResults(ids.size)
    query.setParameter("ids", seqAsJavaList(ids))

    query.getResultList()
  }
}