package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import org.hibernate.annotations.Index
import org.hibernate.annotations.Type

import annotation.target.field

@Embeddable
case class AlertState {
  @Index(name="alertStateDateIndex")
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var date: DateTime =_

  @Index(name="alertStatusIndex")
  @Column(name = "status")
  private[model] var statusName: String = _

  def status = AlertStatus.withName(statusName)

  @Column
  var description: String = _

  @Column()
  var statusCount: Int = _
}

object AlertState {
  import AlertStatus._
  def apply(status: AlertStatus, description: String, date: DateTime, statusCount: Int = 1) = {
    val state = new AlertState()
    state.statusName = status.toString
    state.description = description
    state.date = date
    state.statusCount = statusCount
    state
  }
}

object AlertStatus extends Enumeration {
  type AlertStatus = Value

  val New, Sent, Failed, Abandoned = Value
}
