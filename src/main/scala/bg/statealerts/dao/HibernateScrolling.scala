package bg.statealerts.dao

import org.hibernate.ScrollableResults
import org.hibernate.Session
import org.hibernate.StatelessSession

import javax.persistence.EntityManager

trait HibernateScrolling {

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
