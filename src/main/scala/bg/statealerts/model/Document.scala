package bg.statealerts.model

import org.codehaus.jackson.map.ext.JodaSerializers
import org.hibernate.annotations.Type
import org.joda.time.DateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.GenerationType
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@Entity
case class Document {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = _

  var title: String = _
  @Lob
  var content: String = _
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  //@JsonSerialize(using=classOf[JodaSerializers])
  var publishDate: DateTime = _
  var sourceName: String = _
  var url: String = _
}