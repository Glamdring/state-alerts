package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import bg.statealerts.model.AlertExecution
import bg.statealerts.model.AlertTrigger
import bg.statealerts.model.AlertPeriod
import bg.statealerts.model.AlertPeriod._

@Repository
class AlertTriggerDao extends BaseDao {
  @Value("${alert.triggers.fetch.size:1000}")
  var fetchSize: Int = _

  def deleteAlertTriggers(alertId: Int) = {
    val query = entityManager.createQuery("delete from AlertTrigger where alert.id=:alertId")
    query.setParameter("alertId", alertId)
    query.executeUpdate()
  }

  def performBatched(before: DateTime)(f: (AlertExecution, AlertTrigger) => Unit) {
    performBatched[Array[Object]](
      query = """
                  select
                    t, a.name, a.email, a.period, a.keywords
                  from
                    AlertTrigger t join t.alert a
                  where
                    t.nextExecutionTime <= :before
                  group by
                    a.id
                  order by
                    a.email, a.name, a.id
              """,
      params = Map("before" -> before),
      pageSize = fetchSize,
      Some(CacheOptions(
        cacheable = Some(false),
        cacheMode = Some("GET")
      ))
    )( row => {
        val alertTrigger = row(0).asInstanceOf[AlertTrigger]
        val alertExecution = AlertExecution(
          name = row(1).asInstanceOf[String],
          email = row(2).asInstanceOf[String],
          period = AlertPeriod.withName(row(3).asInstanceOf[String]),
          keywords = row(4).asInstanceOf[String])
        f(alertExecution, alertTrigger)
      })
  }
}
