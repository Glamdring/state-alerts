package bg.statealerts.services

import javax.inject.Inject
import bg.statealerts.model.Alert
import org.springframework.stereotype.Service
import bg.statealerts.model.User
import javax.annotation.Resource
import org.springframework.transaction.annotation.Transactional
import bg.statealerts.dao.AlertDao
import bg.statealerts.dao.AlertLogDao
import bg.statealerts.model.AlertLog
import org.joda.time.Interval
import bg.statealerts.model.AlertState
import bg.statealerts.model.AlertStatus._
import bg.statealerts.model.AlertPeriod._
import scala.collection.JavaConversions
import org.joda.time.DateTime
import bg.statealerts.model.AlertPeriod
import bg.statealerts.model.AlertInfo

@Service
class AlertService {

  @Inject
  var dao: AlertDao = _

  @Inject
  var alertLogDao: AlertLogDao = _

  @Transactional
  def saveAlert(alert: Alert, user: User) = {
    alert.user = user

    dao.save(alert)
  }

  @Transactional(readOnly = true)
  def getAllAlerts(): Seq[AlertInfo] = alertLogDao.getAlerts

  @Transactional(readOnly = true)
  def getAlerts(user: User): List[Alert] = {
    dao.getListByPropertyValue(classOf[Alert], "user", user)
  }

  @Transactional
  def delete(id: Int) = {
    dao.delete(classOf[Alert], id)
  }

  @Transactional
  def prepareAlertLog(alertInfo: AlertInfo, now: DateTime = DateTime.now(), initialStatus: AlertStatus = New): Option[AlertLog] = {

    val from = alertInfo.period match {
      case Daily   => now.minusDays(1)
      case Weekly  => now.minusWeeks(1)
      case Monthly => now.minusMonths(1)
    }
    val interval: Interval = new Interval(from, now)

    val alertLog = new AlertLog()
    alertLog.name = alertInfo.name
    alertLog.email = alertInfo.email
    alertLog.interval = interval
    alertLog.keywords = alertInfo.keywords
    alertLog.state = AlertState(now, initialStatus)
    // TODO retun none if this alert is already pending... or add instead of option introduce new status??? 

    Some(alertLogDao.save(alertLog))
  }

  @Transactional
  def updateAlertLogStatus(alertLog: AlertLog, status: AlertStatus, date: DateTime = DateTime.now) = {
    val statusNotChanged = alertLog.state.status == status
    val statusCount = if (statusNotChanged) alertLog.state.statusCount + 1 else 1
    alertLog.state = AlertState(date, status, statusCount)
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