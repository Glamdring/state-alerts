package bg.statealerts.dao

import scala.collection.JavaConversions

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query
import scala.reflect.ClassTag

trait BaseDao {

  @PersistenceContext
  var entityManager: EntityManager = _

  def save[T](entity: T): T = {
    entityManager.merge(entity);
  }

  def get[T](clazz: Class[T], id: Any): T = {
    entityManager.find(clazz, id);
  }
  
  def delete[T](clazz: Class[T], id: Any) = {
    val entity = get(clazz, id)
    if (entity != null) {
    	entityManager.remove(entity);
    }
  }


  def getResult[T](result: List[T]): Option[T] = {
    if (!result.isEmpty) {
      return Some(result(0))
    }
    return None
  }

  def findByQuery[T: ClassTag](details: QueryDetails): List[T] = {
    var q: Query = null;
    if (details.queryName != null) {
      q = entityManager.createNamedQuery(details.queryName);
    } else if (details.query != null) {
      q = entityManager.createQuery(details.query);
    } else {
      throw new IllegalArgumentException("Either query or query name must be set");
    }

    for (i <- 0 until details.paramNames.length) {
      q.setParameter(details.paramNames(i), details.paramValues(i))
    }
    if (details.start > -1) {
      q.setFirstResult(details.start);
    }
    if (details.count > -1) {
      q.setMaxResults(details.count);
    }
    if (details.cacheable) {
      q.setHint("org.hibernate.cacheable", true);
    }
    return JavaConversions.asScalaBuffer(q.getResultList()).toList.asInstanceOf[List[T]];
  }

  def getByPropertyValue[T](clazz: Class[T], propertyName: String, propertyValue: Any): Option[T] = {
    val dotlessPropertyName = propertyName.replace(".", "");
    val details = new QueryDetails()
    details.query = "SELECT ob FROM " + clazz.getName() + " ob WHERE " + propertyName + "=:" + dotlessPropertyName
    details.paramNames = Array(dotlessPropertyName)
    details.paramValues = Array(propertyValue)
    val result = findByQuery(details)

    return getResult(result);
  }
  
    def getListByPropertyValue[T](clazz: Class[T], propertyName: String, propertyValue: Object): List[T] = {
        val dotlessPropertyName = propertyName.replace(".", "")
        
        val details = new QueryDetails()
        details.query = "SELECT o FROM " + clazz.getName() + " o WHERE " + propertyName + "=:" + dotlessPropertyName
        details.paramNames = Array(dotlessPropertyName)
        details.paramValues = Array(propertyValue)
        val result = findByQuery(details)

        return result;
    }
    
    
    def listPaged[T](clazz: Class[T], start: Int, pageSize: Int): List[T] = {
      val details = new QueryDetails()
      details.query = "from " + clazz.getName() + " ORDER BY id"
      details.start = start
      details.count = pageSize
      return findByQuery(details);
    }
    
    def performBatched[T](clazz: Class[T], pageSize: Int, operation: List[T] => Unit): Unit = {
      var page = 0;
	    while (true) {
	        val data = listPaged(clazz, page * pageSize, pageSize);
	        page += 1;
	        operation(data);
	        // final batch
	        if (data.size < pageSize) {
	            return
	        }
	    }
    }
}