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
  var id: Int
  @Column
  var email: String
  @Column
  var name: String
  @ElementCollection
  var keywords: List[String] = List();
  
}