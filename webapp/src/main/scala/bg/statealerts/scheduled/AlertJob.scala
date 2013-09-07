package bg.statealerts.scheduled

import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import bg.statealerts.model.AlertLog
import bg.statealerts.model.AlertStatus._
import bg.statealerts.model.Document
import bg.statealerts.services.AlertService
import bg.statealerts.services.MailService
import bg.statealerts.services.SearchService
import bg.statealerts.util.TestProfile
import javax.inject.Inject
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import bg.statealerts.util.Logging

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

  
  @Scheduled(cron = "${alert.job.prepare.schedule:0 0 0 * * *}")
  def send() {
    log.debug("start sending alerts")
    val now = DateTime.now
    for (alertInfo <- alertService.getAllAlerts()) {
      val maybeAlertLog = alertService.prepareAlertLog(alertInfo, now)
      if (maybeAlertLog.isDefined) {
          sendAlert(maybeAlertLog.get);
      }
    }
    log.debug("done sending alerts")
  }

  @Scheduled(cron = "${alert.job.prepare.schedule:0 0 12 * * *}")
  def resendFailed() {
    for (alertLog <- alertService getAlertLogsWithStatus Failed) {
      if (alertLog.state.statusCount > maxFailures) {
        alertService.updateAlertLogStatus(alertLog, Abandoned)
      }
      else {
        sendAlert(alertLog)
      }
    }
  }

  def sendAlert(alertLog: AlertLog) = {
    val documents = searchService.search(alertLog.keywords, alertLog.interval)
    val mailSent = mailService.send(prepareMail(alertLog.email, alertLog.name, documents))
    val status = if (mailSent) Sent else Failed
    alertService.updateAlertLogStatus(alertLog, status)
  }

  // TODO: use some templating for mails.
  private def prepareMail(email: String, alertName: String, documents: Seq[Document])(message: MimeMailMessage) {
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

