package bg.statealerts.model

import org.hibernate.annotations.Columns
import org.hibernate.annotations.Type
import org.joda.time.Interval
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.GenerationType
import javax.persistence.FetchType
import org.hibernate.annotations.Index

@Entity
case class AlertLog() {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = _

  @ManyToOne(fetch = FetchType.LAZY)
  var alertTrigger: AlertTrigger = _

  @Column
  var name: String = _

  @Column
  var email: String = _

  @Columns(columns = Array(new Column(name = "begin"), new Column(name = "end")))
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentInterval")
  var interval: Interval = _

  @Column
  var keywords: String = _
  @Embedded
  var state: AlertState = _

}

