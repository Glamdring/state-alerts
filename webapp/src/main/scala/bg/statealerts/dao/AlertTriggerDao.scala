package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

import bg.statealerts.model.Alert
import bg.statealerts.model.AlertTrigger
import javax.persistence.Entity

@Repository
class AlertTriggerDao extends BaseDao {
  @Value("${alert.triggers.fetch.size:1000}")
  var fetchSize: Int = _

  def deleteAlertTriggers(alertId: Int) = {
    val query = entityManager.createQuery("delete from AlertTrigger where alert.id=:alertId")
    query.setParameter("alertId", alertId)
    query.executeUpdate()
  }

  def performBatched(before: DateTime)(f: (Alert, AlertTrigger) => Unit) {
    performBatched[Array[Object]](
      query = """
                  select
                    t, a
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
        val alert = row(1).asInstanceOf[Alert]
        f(alert, alertTrigger)
      })
  }
}
