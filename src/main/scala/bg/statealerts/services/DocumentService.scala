package bg.statealerts.services

import org.springframework.stereotype.Service
import javax.inject.Inject
import bg.statealerts.dao.DocumentDao
import org.springframework.transaction.annotation.Transactional

@Service
class DocumentService {

  @Inject
  var dao: DocumentDao = _
  
  @Transactional
  def save[T](e: T): T = {
    dao.save(e)
  }
}