package bg.statealerts.model

import scala.beans.BeanProperty
import org.hibernate.annotations.Index
import org.hibernate.annotations.Type
import org.joda.time.DateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.GenerationType

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
  @Index(name="documentSourceKeyIndex")
  var sourceKey: String = _
  
  @BeanProperty
  var sourceDisplayName: String = _
  
  @Column(length=2000)
  @BeanProperty
  var url: String = _
  
  @BeanProperty
  var externalId: String = _
  
  @Type(`type`="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var importTime:DateTime = _
}