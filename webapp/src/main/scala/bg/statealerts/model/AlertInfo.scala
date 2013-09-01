package bg.statealerts.model

import bg.statealerts.model.AlertPeriod._
import org.joda.time.DateTime

case class AlertInfo(name: String, email: String, period: AlertPeriod, keywords: String) {

}