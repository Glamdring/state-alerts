package bg.statealerts.dao

import org.joda.time.DateMidnight
import javax.persistence.TypedQuery
import org.joda.time.ReadableDateTime
import org.joda.time.DateTime
import org.springframework.stereotype.Repository

@Repository
class DocumentDao extends BaseDao {
  def getLastImportDate(sourceName: String): Option[ReadableDateTime] = {
    val query: TypedQuery[DateTime] = entityManager.createQuery("SELECT latestDocumentDate FROM Import WHERE sourceName = :sourceName ORDER BY latestDocumentDate DESC", classOf[DateTime])
    query.setMaxResults(1)
    query.setParameter("sourceName", sourceName)

    val result = query.getResultList()
    if (result.isEmpty()) {
      return None
    } else {
      return Some(new DateMidnight(result.get(0)))
    }
  }
}