package bg.statealerts.scheduled

import bg.statealerts.model.AlertLog
import bg.statealerts.model.AlertState
import bg.statealerts.model.AlertStatus._
import bg.statealerts.model.Document
import bg.statealerts.model.AlertExecution
import bg.statealerts.model.AlertTrigger
import bg.statealerts.services.AlertService
import bg.statealerts.services.MailService
import bg.statealerts.services.SearchService
import bg.statealerts.util.Logging
import bg.statealerts.util.TestProfile
import javax.inject.Inject
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.mail.MailException
import bg.statealerts.services.MessagePreparators

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

  @Scheduled(cron = "${alert.job.send.schedule:0 0 0 * * *}")
  def send() {
    log.debug("start preparing alerts")
    val prepareTime = DateTime.now
    alertService.forAlertExecution(prepareTime) {
      (alertExecution: AlertExecution, alertTrigger: AlertTrigger) =>
        val alertLog = alertService.prepareAlertExecution(alertExecution, Some(alertTrigger), prepareTime)
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
    val documents = searchService.search(alertLog.keywords, alertLog.interval)
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

