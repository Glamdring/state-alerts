package bg.statealerts.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Service
import bg.statealerts.util.Logging
import javax.inject.Inject
import javax.mail.internet.MimeMessage
import org.springframework.mail.MailException
import org.slf4j.LoggerFactory
import org.slf4j.Logger

@Service
class MailService @Inject() (val mailSender: JavaMailSender) extends Logging {

  val logger: Logger = LoggerFactory.getLogger(classOf[MailService])
  
  def send(preparator: MimeMailMessage => Unit): Boolean =
    try {
      mailSender.send(new MimeMessagePreparator() {
        def prepare(mimeMessage: MimeMessage) = preparator(new MimeMailMessage(mimeMessage))
      })
      true
    }
    catch {
      case e: MailException => {
        logger.warn("Failed sending email", e);
        false
      }
    }
}
