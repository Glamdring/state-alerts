package bg.statealerts.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.ElementCollection

@Entity
class Alert {

  @Id @GeneratedValue(strategy=GenerationType.AUTO)
  var id: Int = _
  @Column
  var email: String = _
  @Column
  var name: String = _
  @ElementCollection
  var keywords: List[String] = List();
  
}