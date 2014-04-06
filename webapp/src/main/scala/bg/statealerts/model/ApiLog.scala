package bg.statealerts.model

import javax.persistence.GeneratedValue
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GenerationType
import org.joda.time.DateTime
import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.ManyToOne

@Entity
case class ApiLog() {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = _
  
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var searchTime: DateTime = _
  
  @Column
  var keywords: String = _
  
  @Column
  var operationType: String = _
  
  @Column
  var sources: String = _
  
  @ManyToOne
  var user: User = _
  

}