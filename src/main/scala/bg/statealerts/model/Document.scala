package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.persistence.Entity
import javax.persistence.Lob
import org.hibernate.annotations.Type

@Entity
class Document {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = _

  var title: String = _
  @Lob
  var content: String = _
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var publishDate: DateTime = _
  var sourceName: String = _
}