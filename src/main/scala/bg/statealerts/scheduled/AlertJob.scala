package bg.statealerts.scheduled

import scala.collection.JavaConversions._

import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import bg.statealerts.model.Alert
import bg.statealerts.model.Document
import bg.statealerts.services.AlertService
import bg.statealerts.services.MailService
import bg.statealerts.services.SearchService
import javax.inject.Inject

@Component
class AlertJob {

  @Inject
  var alertService: AlertService = _

  @Inject
  var mailService: MailService = _

  @Inject
  var searchService: SearchService = _

  //TODO: @Scheduled()
  def run() {
    for (alert <- alertService.getAllAlerts) {
      val keywords = alert.keywords.mkString(" ")
      val documents = searchService.search(keywords)
      mailService.send(prepareMail(alert, documents))
    }
  }

  // TODO: use some templating for mails.
  private def prepareMail(alert: Alert, documents: Seq[Document])(message: MimeMailMessage) {
      def subject() =
        {
          val alertName = alert.name
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
    message.setTo(alert.email)
    message.setSubject(subject())
    message.setText(text())
  }
}

