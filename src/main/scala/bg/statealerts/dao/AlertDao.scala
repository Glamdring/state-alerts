package bg.statealerts.dao

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import bg.statealerts.model.Alert

trait AlertDao {
  def getAlerts(): Seq[Alert]
}

trait HibernateScrolling {
  import javax.persistence.EntityManager
  import org.hibernate.ScrollableResults
  import org.hibernate.StatelessSession
  import org.hibernate.Session

  var entityManager: EntityManager

  def stream[T](query: StatelessSession => ScrollableResults): Stream[T] = {

    val session = entityManager.unwrap(classOf[Session]).getSessionFactory().openStatelessSession()
    val results = query(session)

      def stream0(): Stream[T] =
        if (results.next()) {
          Stream.cons(results.get(0).asInstanceOf[T], stream0())
        }
        else {
          results.close()
          session.close()
          Stream.empty
        }
    stream0()
  }

}

@Repository
class HibernateAlertDao extends BaseDao with HibernateScrolling with AlertDao {

  @Value("1000")
  var fetchSize: Int = _

  def getAlerts(): Stream[Alert] = stream(session => {
    import org.hibernate.LockMode
    import org.hibernate.ScrollMode

    val query =
      session
        .createQuery("from Alert order by email, name, id")
        .setFetchSize(fetchSize)
        .setReadOnly(true)
        .setLockMode("a", LockMode.NONE)

    query.scroll(ScrollMode.FORWARD_ONLY)
  })

}
