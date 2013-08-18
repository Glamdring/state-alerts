package bg.statealerts.model

import java.util.ArrayList
import scala.beans.BeanProperty
import org.hibernate.annotations.LazyCollection
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.GenerationType
import org.hibernate.annotations.LazyCollectionOption

@Entity
class Alert {

  @Id @GeneratedValue(strategy=GenerationType.AUTO)
  @BeanProperty
  var id: Int = _
  @Column
  @BeanProperty
  var email: String = _
  @Column
  @BeanProperty
  var name: String = _
  
  @ElementCollection
  @LazyCollection(LazyCollectionOption.FALSE)
  @BeanProperty
  var keywords: java.util.List[String] = new ArrayList();
  
  @ElementCollection
  @LazyCollection(LazyCollectionOption.FALSE)
  @BeanProperty
  var sources: java.util.List[String] = new ArrayList();

  @BeanProperty
  var period: String = _
    
  @ManyToOne
  @BeanProperty
  var user: User = _
  
  def getPeriodValue(): AlertPeriod.AlertPeriod = { // couldn't get scala to work with both spring-mvc and hibernate convertions to enum
    AlertPeriod.withName(period)
  }
}