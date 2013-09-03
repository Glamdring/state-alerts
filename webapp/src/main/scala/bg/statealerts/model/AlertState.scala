package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import org.hibernate.annotations.Type

import annotation.target.field

@Embeddable
case class AlertState {
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var date: DateTime =_

  @Column(name = "status")
  private[model] var statusName: String = _

  def status = AlertStatus.withName(statusName)

  @Column()
  var statusCount: Int = _
}

object AlertState {
  import AlertStatus._
  def apply(date: DateTime, status: AlertStatus, statusCount: Int = 1): AlertState = {
    val state = new AlertState()
    state.date = date
    state.statusName = status.toString
    state.statusCount = statusCount
    state
  }
}

object AlertStatus extends Enumeration {
  type AlertStatus = Value

  val New, Sent, Failed, Abandoned = Value
}
