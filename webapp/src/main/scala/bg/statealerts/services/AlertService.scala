package bg.statealerts.services

import javax.inject.Inject
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import org.joda.time.Interval
import org.joda.time.DateTime
import bg.statealerts.model.Alert
import bg.statealerts.model.User
import bg.statealerts.model.AlertLog
import bg.statealerts.model.AlertState
import bg.statealerts.model.AlertStatus._
import bg.statealerts.model.AlertPeriod._
import bg.statealerts.model.AlertPeriod
import bg.statealerts.model.AlertTrigger
import bg.statealerts.model.AlertExecution
import bg.statealerts.dao.AlertDao
import bg.statealerts.dao.AlertLogDao
import bg.statealerts.dao.AlertTriggerDao

@Service
class AlertService {

  @Inject
  var dao: AlertDao = _

  @Inject
  var alertLogDao: AlertLogDao = _

  @Inject
  var alertTriggerDao: AlertTriggerDao = _

  @Transactional
  def saveAlert(alert: Alert, user: User) = {
    alert.user = user
    val result = dao.save(alert)
    val alertTrigger = new AlertTrigger
    alertTrigger.alert = result
    alertTrigger.nextExecutionTime = AlertTrigger.nextExecutionTime(alert.getPeriodValue)
    alertTriggerDao.save(alertTrigger)
    result
  }

  @Transactional(readOnly = true)
  def forAlertExecution(before: DateTime)(f: (AlertExecution, AlertTrigger) => Unit) =
    alertTriggerDao.performBatched(before)(f)

  @Transactional(readOnly = true)
  def getAlerts(user: User): Seq[Alert] = {
    dao.getListByPropertyValue(classOf[Alert], "user", user)
  }

  @Transactional
  def delete(id: Int) = {
    alertTriggerDao.delete(classOf[AlertTrigger], id)
    dao.delete(classOf[Alert], id)
  }

  protected def getInitialFrom(alertExecution: AlertExecution, maybeTrigger: Option[AlertTrigger], time: DateTime) = {
    if (maybeTrigger.isDefined && Option(maybeTrigger.get.lastExecutionTime).isDefined) {
      maybeTrigger.get.lastExecutionTime
    } else {
      AlertTrigger.lastExecutionTime(alertExecution.period, time)
    }
  }

  @Transactional
  def prepareAlertExecution(alertExecution: AlertExecution, maybeTrigger: Option[AlertTrigger], prepareTime: DateTime): AlertLog = {

    val now = DateTime.now
    val from = getInitialFrom(alertExecution, maybeTrigger, prepareTime)

    val interval: Interval = new Interval(from, prepareTime)


    val alertLog = new AlertLog()
    alertLog.name = alertExecution.name
    alertLog.email = alertExecution.email
    alertLog.interval = interval
    alertLog.keywords = alertExecution.keywords
    alertLog.state = AlertState(New, "Prepared", now)

    if (maybeTrigger.isDefined)
    {
        val trigger = maybeTrigger.get
        trigger.lastExecutionTime = prepareTime
        trigger.nextExecutionTime = AlertTrigger.nextExecutionTime(alertExecution.period, prepareTime)
        alertTriggerDao.save(trigger)
    }
    alertLogDao.save(alertLog)
  }

  @Transactional
  def updateAlertLogStatus(alertLog: AlertLog, status: AlertStatus, description: String) = {
    val statusNotChanged = alertLog.state.status == status
    val statusCount = if (statusNotChanged) alertLog.state.statusCount + 1 else 1
    alertLog.state = AlertState(status, description, DateTime.now, statusCount)
    alertLogDao.save(alertLog)
  }

  @Transactional(readOnly = true)
  def getAlertLogsWithStatus(status: AlertStatus): Seq[AlertLog] = {
    alertLogDao.getAlertLogsWithStatus(status)
  }

  @Transactional
  def deleteLogsBefore(when: DateTime) = {
    alertLogDao.deleteLogsBefore(when)
  }
}