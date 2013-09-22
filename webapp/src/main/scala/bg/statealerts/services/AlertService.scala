package bg.statealerts.services

import org.joda.time.DateTime
import org.joda.time.Interval
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import bg.statealerts.dao.AlertDao
import bg.statealerts.dao.AlertLogDao
import bg.statealerts.dao.AlertTriggerDao
import bg.statealerts.model.Alert
import bg.statealerts.model.AlertLog
import bg.statealerts.model.AlertPeriod
import bg.statealerts.model.AlertState
import bg.statealerts.model.AlertStatus.AlertStatus
import bg.statealerts.model.AlertStatus.New
import bg.statealerts.model.AlertTrigger
import bg.statealerts.model.User
import javax.inject.Inject
import javax.persistence.Entity

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
    val persisted = dao.save(alert)
    alertTriggerDao.save(AlertTrigger(persisted))
    persisted
  }

  @Transactional(readOnly = true)
  def performBatched(before: DateTime)(f: (Alert, AlertTrigger) => Unit) =
    alertTriggerDao.performBatched(before)(f)

  @Transactional(readOnly = true)
  def getAlerts(user: User): Seq[Alert] = {
    dao.getListByPropertyValue(classOf[Alert], "user", user)
  }

  @Transactional
  def delete(id: Int) = {
    alertTriggerDao.deleteAlertTriggers(id)
    dao.delete(classOf[Alert], id)
  }

  protected def getInitialFrom(alert: Alert, maybeTrigger: Option[AlertTrigger], time: DateTime) = {
    if (maybeTrigger.isDefined && Option(maybeTrigger.get.lastExecutionTime).isDefined) {
      maybeTrigger.get.lastExecutionTime
    } else {
      AlertTrigger.lastExecutionTime(AlertPeriod.withName(alert.period), time)
    }
  }

  @Transactional
  def prepareAlertExecution(alert: Alert, maybeTrigger: Option[AlertTrigger], prepareTime: DateTime): AlertLog = {

    val now = DateTime.now
    val from = getInitialFrom(alert, maybeTrigger, prepareTime)

    val interval: Interval = new Interval(from, prepareTime)


    val alertLog = new AlertLog()
    alertLog.name = alert.name
    alertLog.email = alert.email
    alertLog.interval = interval
    alertLog.keywords = alert.keywords
    alertLog.state = AlertState(New, "Prepared", now)
    alertLog.sources = alert.sources;
    
    if (maybeTrigger.isDefined)
    {
        val trigger = maybeTrigger.get
        trigger.lastExecutionTime = prepareTime
        trigger.nextExecutionTime = AlertTrigger.nextExecutionTime(AlertPeriod.withName(alert.period), prepareTime)
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