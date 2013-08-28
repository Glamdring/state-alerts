package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.stereotype.Repository
import bg.statealerts.model.AlertLog
import scala.collection.JavaConversions
import bg.statealerts.model.AlertState
import org.springframework.beans.factory.annotation.Value
import bg.statealerts.model.AlertPeriod
import bg.statealerts.model.AlertStatus._
import bg.statealerts.model.AlertInfo

trait AlertLogDao extends BaseDao {

  def deleteLogsBefore(when: DateTime): Int = {
    val query = entityManager.createQuery("delete from AlertLog where state.date < :when")
    query.setParameter("when", when)
    query.executeUpdate()
  }

  def getAlertLogsWithState(status: AlertStatus) = {
    val query = entityManager.createQuery[AlertLog]("from AlertLog where state = :state.status order by status.date", classOf[AlertLog])
    query.setParameter("state", status.toString())
    JavaConversions.asScalaBuffer(query.getResultList())
  }

  def getAlerts(): Seq[AlertInfo]
}

@Repository("alertLogDao")
class HibernateAlertLogDao extends HibernateScrolling with AlertLogDao {

  @Value("1000")
  var fetchSize: Int = _

  def getAlerts(): Stream[AlertInfo] = stream(session => {
    import org.hibernate.LockMode
    import org.hibernate.ScrollMode

    val query =
      session
        .createQuery("select a.name, a.email, a.period, string_agg(k.id) from Alert a left join a.keywords k group by a.id order by a.email, a.name, a.id ")
        .setFetchSize(fetchSize)
        .setReadOnly(true)
        .setLockMode("l", LockMode.NONE)

    query.scroll(ScrollMode.FORWARD_ONLY)
  })(row => {
    AlertInfo(
      name = row(0).asInstanceOf[String],
      email = row(1).asInstanceOf[String],
      period = AlertPeriod.withName(row(2).asInstanceOf[String]),
      keywords = row(3).asInstanceOf[String])
  })

}


