package bg.statealerts.model

import bg.statealerts.model.AlertPeriod._

case class AlertInfo(name: String, email: String, period: AlertPeriod, keywords: String) {

}