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

@Component
@TestProfile.Disabled
class AlertJob extends Logging {

  @Inject
  var alertService: AlertService = _

  @Inject
  var mailService: MailService = _

  @Inject
  var searchService: SearchService = _

  @Value("${alert.job.max_failures:5}")
  var maxFailures: Int = _

  @Value("${mail.address}")
  var from: String = _

  @Scheduled(cron = "${alert.job.send.schedule:0 0 0 * * *}")
  def send() {
    log.debug("start preparing alerts")
    val prepareTime = DateTime.now
    alertService.forAlertExecution(prepareTime) {
      (alertExecution: AlertExecution, alertTrigger: AlertTrigger) =>
        val alertLog = alertService.prepareAlertExecution(alertExecution, Some(alertTrigger), prepareTime)
        if (alertLog.state.status == New) {
            sendAlert(alertLog);
        } else {
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
        val successfullySent = mailService.send(prepareMail(alertLog.email, alertLog.name, alertLog.state, documents))
        if (successfullySent) {
          val count = documents.size
          (Sent, s"Mail with $count documents successfully sent.")
        }
        else {
          if (alertLog.state.status == Failed && alertLog.state.statusCount > maxFailures) {
            (Abandoned, s"Maximum failures ($maxFailures) exceeded.")
          }
          else {
            (Failed, "Mail sending failed.")
          }
        }
      }
    alertService.updateAlertLogStatus(alertLog, status, description)
  }

  // TODO: use some templating for mails.
  private def prepareMail(email: String, alertName: String, alertState: AlertState, documents: Seq[Document])(message: MimeMailMessage) {
      def subject() =
        {
          val numberOfDocuments = documents.size
          s"[state-alerts] $numberOfDocuments documents matched your $alertName alert"
        }
      def text() = {
        documents.map((d: Document) => {
          val title = d.title
          val url = d.url
          s"$title [$url]"
        }).mkString("\n")
      }
    message.setFrom(from);
    message.setTo(email)
    message.setSubject(subject())
    message.setText(text())
  }
}

