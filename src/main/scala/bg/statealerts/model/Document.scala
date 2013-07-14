package bg.statealerts.model

import org.joda.time.DateTime
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.persistence.Entity

@Entity
class Document {
   @Id @GeneratedValue(strategy=GenerationType.AUTO)
   var id: Int = _
  
	var title: String = _
	var content: String = _
	var publishDate: DateTime = _
	var source: String = _
}