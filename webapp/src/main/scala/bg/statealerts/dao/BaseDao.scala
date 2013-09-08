package bg.statealerts.dao

import scala.collection.JavaConversions
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query
import scala.reflect.ClassTag
import scala.annotation.tailrec

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

  def getResult[T](result: Seq[T]): Option[T] = {
    if (!result.isEmpty) {
      return Some(result(0))
    }
    return None
  }

  def findByQuery[T: ClassTag](details: BaseQueryDetails): Seq[T] = {
    val q = details match {
      case namedQueryDetails: NamedQueryDetails => entityManager.createNamedQuery(namedQueryDetails.queryName)
      case queryDetails: QueryDetails           => entityManager.createQuery(queryDetails.query)
    }

    for ((name, value) <- details.params) {
      q.setParameter(name, value)
    }

    details.pagination match {
      case NoPagination =>
      case DefaultPagination(start, count) => {
        q.setFirstResult(start)
        q.setMaxResults(count)
      }
    }
    if (details.cacheOptions.isDefined) {
      if (details.cacheOptions.get.cacheable.isDefined) {
          q.setHint("org.hibernate.cacheable", details.cacheOptions.get.cacheable.get)
      }
      if (details.cacheOptions.get.cacheMode.isDefined) {
          q.setHint("org.hibernate.cacheMode", details.cacheOptions.get.cacheMode.get)
      }
    }
    return JavaConversions.asScalaBuffer(q.getResultList()).asInstanceOf[Seq[T]];
  }

  def getByPropertyValue[T](clazz: Class[T], propertyName: String, propertyValue: Any): Option[T] = {
    val dotlessPropertyName = propertyName.replace(".", "");
    val details =
      QueryDetails(
        query = "SELECT ob FROM " + clazz.getName() + " ob WHERE " + propertyName + "=:" + dotlessPropertyName,
        params = Map(dotlessPropertyName -> propertyValue))
    val result = findByQuery(details)

    return getResult(result);
  }

  def getListByPropertyValue[T](clazz: Class[T], propertyName: String, propertyValue: Object): Seq[T] = {
    val dotlessPropertyName = propertyName.replace(".", "")

    val details =
      QueryDetails(
        query = "SELECT o FROM " + clazz.getName() + " o WHERE " + propertyName + "=:" + dotlessPropertyName,
        params = Map(dotlessPropertyName -> propertyValue))
    findByQuery(details)
  }

  protected def typePageQuery[T](clazz: Class[T]): String = "from " + clazz.getName() + " ORDER BY id"

  private def loadPage[T: ClassTag](query: => String, params: Map[String, Any], cacheOptions: Option[CacheOptions], start: Int, pageSize: Int): Seq[T] = {
    findByQuery[T](QueryDetails(
      query = query,
      params = params,
      pagination = DefaultPagination(start, pageSize),
      cacheOptions = cacheOptions));
  }

  def performBatched[T: ClassTag](clazz: Class[T], pageSize: Int)(operation: T => Unit): Unit = {
    performBatched(typePageQuery(clazz), Map(), pageSize, None)(operation)
  }

  def performBatched[T: ClassTag](query: String, params: Map[String, Any], pageSize: Int, cacheOptions: Option[CacheOptions] = None)(operation: T => Unit): Unit = {
      @tailrec
      def paginate(currentPage: Int) {
        val page = loadPage[T](query, params, cacheOptions, currentPage * pageSize, pageSize)
        for (data <- page) {
          operation(data)
        }
        if (page.size == pageSize) {
          paginate(currentPage + 1)
        }
      }
    paginate(0)
  }
}