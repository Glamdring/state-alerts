package bg.statealerts.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Service

import bg.statealerts.util.Logging
import javax.inject.Inject
import javax.mail.internet.MimeMessage

@Service
class MailService @Inject() (val mailSender: JavaMailSender) extends Logging {

  @Value("${mail.maxBatchSize:500}")
  var maxBatchSize: Int = _
  
  implicit def func2MailPreparator(f: MimeMailMessage => Unit) = new MimeMessagePreparator() {
      def prepare(mimeMessage: MimeMessage) = f(new MimeMailMessage(mimeMessage))
  }
  
  // TODO: think about how to send batches in a good scala way. i.e. workaround the arrays in the api. 
  def send(f: MimeMailMessage => Unit) = mailSender.send(f)
  

}

