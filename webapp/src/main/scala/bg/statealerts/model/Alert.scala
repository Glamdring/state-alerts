package bg.statealerts.model

import java.util.ArrayList
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.GenerationType
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption

@Entity
class Alert {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = _

  @Column
  var email: String = _

  @Column
  var name: String = _

  @Column
  var keywords: String = _

  @ElementCollection
  @LazyCollection(LazyCollectionOption.FALSE)
  var sources: java.util.List[String] = new ArrayList();

  var period: String = _

  @ManyToOne
  var user: User = _

  def getPeriodValue(): AlertPeriod.AlertPeriod = { // couldn't get scala to work with both spring-mvc and hibernate convertions to enum
    AlertPeriod.withName(period)
  }
}