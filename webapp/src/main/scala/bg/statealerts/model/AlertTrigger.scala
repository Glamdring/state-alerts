package bg.statealerts.model

import org.hibernate.annotations.Index
import org.hibernate.annotations.Type
import org.joda.time.DateTime
import bg.statealerts.model.AlertPeriod.AlertPeriod
import bg.statealerts.model.AlertPeriod.Daily
import bg.statealerts.model.AlertPeriod.Monthly
import bg.statealerts.model.AlertPeriod.Weekly
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.GenerationType
import javax.persistence.FetchType

@Entity
class AlertTrigger extends Serializable {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = _

  @ManyToOne(fetch=FetchType.LAZY)
  var alert: Alert = _

  @Index(name = "alertTriggerNextDateIndex")
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var nextExecutionTime: DateTime = _

  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var lastExecutionTime: DateTime = _

}

object AlertTrigger {

  def nextExecutionTime(period: AlertPeriod, date: DateTime = DateTime.now) = period match {
    case Daily   => date.plusDays(1)
    case Weekly  => date.plusWeeks(1)
    case Monthly => date.plusMonths(1)
  }

  def lastExecutionTime(period: AlertPeriod, date: DateTime = DateTime.now) =
    period match {
      case Daily   => date.minusDays(1)
      case Weekly  => date.minusWeeks(1)
      case Monthly => date.minusMonths(1)
    }
}