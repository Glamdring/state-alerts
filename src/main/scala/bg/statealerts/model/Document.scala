package bg.statealerts.model

import org.hibernate.annotations.Type
import org.joda.time.DateTime
import javax.persistence._

@Entity
case class Document {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = _

  @Column(length=2000)
  var title: String = _
  @Lob
  var content: String = _
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  //@JsonSerialize(using=classOf[JodaSerializers])
  var publishDate: DateTime = _
  var sourceName: String = _
  var url: String = _
}