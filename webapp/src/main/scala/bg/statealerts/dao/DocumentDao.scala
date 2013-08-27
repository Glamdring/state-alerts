package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.stereotype.Repository

import javax.persistence.TypedQuery

@Repository
class DocumentDao extends BaseDao {
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
}