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
  def prepareAlertLog(alert: AlertInfo, initialStatus: AlertStatus = Pending): AlertLog = {

    val now = DateTime.now()
    val from = alert.period match {
      case Daily   => now.minusDays(1)
      case Weekly  => now.minusWeeks(1)
      case Monthly => now.minusMonths(1)
    }
    val interval: Interval = new Interval(from, now)

    val alertLog = new AlertLog()
    alertLog.name = alert.name
    alertLog.email = alert.email
    alertLog.interval = interval
    alertLog.keywords = alert.keywords
    alertLog.state = AlertState(now, initialStatus)
    
    alertLogDao.save(alertLog)
  }

  @Transactional
  def updateAlertLogStatus(alertLog: AlertLog, status: AlertStatus) = {
    alertLog.state = AlertState(DateTime.now(), status)
    alertLogDao.save(alertLog)
  }

  @Transactional(readOnly = true)
  def getAlertLogs(status: AlertStatus): Seq[AlertLog] = {
    alertLogDao.getAlertLogsWithState(status)
  }

  @Transactional
  def deleteLogsBefore(when: DateTime) = {
    alertLogDao.deleteLogsBefore(when)
  }
}