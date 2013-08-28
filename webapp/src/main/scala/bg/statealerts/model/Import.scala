package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.persistence.Entity
import org.hibernate.annotations.{Index, Type}

@Entity
class Import {
  @Id @GeneratedValue(strategy=GenerationType.AUTO)
  var id: Int = _
  
  @Type(`type`="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var latestDocumentDate:DateTime = _
  @Type(`type`="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  var importTime:DateTime = _
  var importedDocuments:Int = _
  @Index(name="importSourceKeyIndex")
  var sourceKey: String = _
}