package bg.statealerts.model

import java.util.ArrayList
import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.GenerationType
import javax.persistence.FetchType

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
  @ElementCollection(fetch=FetchType.EAGER)
  @BeanProperty
  var keywords: java.util.List[String] = new ArrayList();
  
  @ManyToOne
  @BeanProperty
  var user: User = _
  
}