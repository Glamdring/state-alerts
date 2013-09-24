package bg.statealerts.scheduled

import scala.collection.JavaConversions

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import bg.statealerts.model.Alert
import bg.statealerts.model.AlertLog
import bg.statealerts.model.AlertStatus.Abandoned
import bg.statealerts.model.AlertStatus.Failed
import bg.statealerts.model.AlertStatus.New
import bg.statealerts.model.AlertStatus.Sent
import bg.statealerts.model.AlertTrigger
import bg.statealerts.services.AlertService
import bg.statealerts.services.MailService
import bg.statealerts.services.MessagePreparators
import bg.statealerts.services.SearchService
import bg.statealerts.util.Logging
import bg.statealerts.util.TestProfile
import javax.inject.Inject
import javax.persistence.Embeddable
import javax.persistence.Entity

@Component
@TestProfile.Disabled
class AlertJob extends Logging {

  @Inject
  var alertService: AlertService = _

  @Inject
  var mailService: MailService = _
  
  @Inject
  var messagePreparators: MessagePreparators = _

  @Inject
  var searchService: SearchService = _

  @Value("${alert.job.max_failures:5}")
  var maxFailures: Int = _

  @Value("${index.refreshRate}")
  var indexRefreshRate: Long = _

  @Scheduled(cron = "${alert.job.send.schedule:0 0 0 * * *}")
  def send() {
    log.debug("start sending alerts")
    val until = DateTime.now.minus(indexRefreshRate)
    alertService.performBatched(until) {
      (alert: Alert, alertTrigger: AlertTrigger) =>
        val alertLog = alertService.prepareAlertExecution(alert, Some(alertTrigger), until)
        if (alertLog.state.status == New) {
          sendAlert(alertLog);
        }
        else {
          // TODO: better logging 
          log.warn("Not sending alert.")
        }
    }
    log.debug("done sending alerts")
  }

  @Scheduled(cron = "${alert.job.resend_failed.schedule:0 0 12 * * *}")
  def resendFailed() {
    log.debug("checking for failed alert logs")
    val failedLogs = alertService.getAlertLogsWithStatus(Failed)
    if (!failedLogs.isEmpty) {
      log.info("{} failed alert logs found. will try to resend them now", failedLogs.size)
    }
    for (alertLog <- failedLogs) {
      sendAlert(alertLog)
    }
  }

  def sendAlert(alertLog: AlertLog) = {
    val documents = searchService.search(alertLog.keywords, alertLog.interval, JavaConversions.asScalaBuffer(alertLog.sources).toList)
    val (status, description) =
      if (documents.isEmpty) {
        (Abandoned, "No matching documents.")
      }
      else {
        try {
          mailService.send(messagePreparators.alertMail(alertLog.email, alertLog.name, alertLog.state, documents))
          val count = documents.size
          (Sent, s"Mail with $count documents successfully sent.")
        }
        catch {
          case e: MailException => {
            log.warn("Failed sending alert email", e);
            if (alertLog.state.status == Failed && alertLog.state.statusCount > maxFailures) {
              (Abandoned, s"Maximum failures ($maxFailures) exceeded.")
            }
            else {
              (Failed, "Mail sending failed.")
            }
          }
        }
      }
    alertService.updateAlertLogStatus(alertLog, status, description)
  }

}

