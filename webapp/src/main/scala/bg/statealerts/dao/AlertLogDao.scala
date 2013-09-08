package bg.statealerts.dao

import org.joda.time.DateTime
import org.springframework.stereotype.Repository
import bg.statealerts.model.AlertLog
import scala.collection.JavaConversions._
import bg.statealerts.model.AlertState
import org.springframework.beans.factory.annotation.Value
import bg.statealerts.model.AlertPeriod
import bg.statealerts.model.AlertStatus._
import bg.statealerts.model.AlertExecution
import bg.statealerts.model.AlertTrigger

@Repository
class AlertLogDao extends BaseDao {

  def deleteLogsBefore(when: DateTime): Int = {
    val query = entityManager.createQuery("delete from AlertLog where state.date < :when")
    query.setParameter("when", when)
    query.executeUpdate()
  }

  def getAlertLogsWithStatus(status: AlertStatus): Seq[AlertLog] = {
    val query = entityManager.createQuery[AlertLog]("from AlertLog where state.statusName = :status order by state.date", classOf[AlertLog])
    query.setParameter("status", status.toString())
    query.getResultList()
  }
}
