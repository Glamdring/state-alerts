package bg.statealerts.services

import org.springframework.stereotype.Component
import bg.statealerts.model.AlertState
import bg.statealerts.model.Document
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.beans.factory.annotation.Value

// TODO: use some templating for mails.
@Component
class MessagePreparators {

  @Value("${mail.address}")
  var from: String = _

  def alertMail(email: String, alertName: String, alertState: AlertState, documents: Seq[Document])(message: MimeMailMessage) {
      def subject() =
        {
          val numberOfDocuments = documents.size
          s"[state-alerts] $numberOfDocuments documents matched your $alertName alert"
        }
      def text() = {
        documents.map((d: Document) => {
          val title = d.title
          val url = d.url
          s"$title [ $url ]"
        }).mkString("\n\n\n")
      }
    message.setFrom(from);
    message.setTo(email)
    message.setSubject(subject())
    message.setText(text())
  }

  def testMail(email: String)(message: MimeMailMessage) {
    message.setFrom(from);
    message.setTo(email)
    message.setSubject("[state-alerts] Test Mail")
    message.setText("This is a State Alerts test mail. Seems like you mail settings are correct.")
  }
}