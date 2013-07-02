package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType

class Import {
   @Id @GeneratedValue(strategy=GenerationType.AUTO)
   var id: Int = _
  
	var importTime:DateTime = _
	var importedDocuments:Int = _
}