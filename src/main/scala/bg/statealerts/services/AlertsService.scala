package bg.statealerts.services

import org.springframework.mail.MailSender
import org.springframework.stereotype.Service

import javax.inject.Inject

@Service
class AlertsService @Inject() (val mailSender: MailSender) {

}