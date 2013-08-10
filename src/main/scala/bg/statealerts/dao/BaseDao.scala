package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import org.joda.time.DateMidnight
import org.joda.time.ReadableDateTime

@Repository
class BaseDao {

  @PersistenceContext
  var entityManager: EntityManager = _
  
  def save[T](entity: T): T = {
    entityManager.merge(entity);
  }
  
  def get[T](clazz: Class[T], id:Any): T = {
    entityManager.find(clazz, id);
  }
 
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