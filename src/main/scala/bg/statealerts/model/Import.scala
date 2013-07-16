package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.persistence.Entity
import org.hibernate.annotations.Type

@Entity
class Import {
   @Id @GeneratedValue(strategy=GenerationType.AUTO)
   var id: Int = _
  
   	@Type(`type`="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	var importTime:DateTime = _
	var importedDocuments:Int = _
}