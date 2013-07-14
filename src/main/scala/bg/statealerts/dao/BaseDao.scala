package bg.statealerts.dao

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import javax.persistence.TypedQuery
import org.joda.time.DateTime

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
 
  def getLastImportDate(): DateTime = {
    val query: TypedQuery[DateTime] = entityManager.createQuery("SELECT importTime FROM Import ORDER BY importTime DESC", classOf[DateTime])
    query.setMaxResults(1)
    val result = query.getResultList()
    if (result.isEmpty()) {
      return null
    } else {
      return result.get(0)
    }
  }
}