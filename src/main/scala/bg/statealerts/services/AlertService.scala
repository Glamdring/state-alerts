package bg.statealerts.services

import javax.inject.Inject
import bg.statealerts.dao.BaseDao
import bg.statealerts.model.Alert
import org.springframework.stereotype.Service
import bg.statealerts.model.User
import javax.annotation.Resource
import org.springframework.transaction.annotation.Transactional

@Service
class AlertService {

  @Resource(name="baseDao")
  var dao: BaseDao = _
  
  @Transactional
  def saveAlert(alert: Alert, user: User) = {
    alert.user = user
    dao.save(alert)
  }
  
  @Transactional(readOnly = true)
  def getAlerts(user: User): List[Alert] = {
	  dao.getListByPropertyValue(classOf[Alert], "user", user)
  }
  
  @Transactional
  def delete(id: Long) = {
    dao.delete(classOf[Alert], id)
  }
}