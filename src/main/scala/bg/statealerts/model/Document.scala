package bg.statealerts.model

import org.hibernate.annotations.Type
import org.joda.time.DateTime
import javax.persistence._
import scala.beans.BeanProperty

@Entity
case class Document {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(length=2000)
  @BeanProperty
  var title: String = _
  
  @Lob
  @BeanProperty
  var content: String = _
  
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @BeanProperty
  //@JsonSerialize(using=classOf[JodaSerializers])
  var publishDate: DateTime = _
  
  @BeanProperty
  var sourceName: String = _
  
  @Column(length=2000)
  @BeanProperty
  var url: String = _
  
  @BeanProperty
  var externalId: String = _
  
  @Type(`type`="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var importTime:DateTime = _
}