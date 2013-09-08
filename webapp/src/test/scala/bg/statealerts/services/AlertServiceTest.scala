package bg.statealerts.services

import scala.collection.JavaConversions.seqAsJavaList
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional
import bg.statealerts.model.Alert
import bg.statealerts.model.User
import bg.statealerts.util.TestProfile
import javax.persistence.Entity
import javax.inject.Inject
import bg.statealerts.model.AlertPeriod._
import org.junit.Assert
import org.hamcrest.CoreMatchers
import org.joda.time.DateTime
import org.apache.commons.lang3.ArrayUtils
import scala.collection.mutable.ListBuffer
import bg.statealerts.model.AlertExecution
import bg.statealerts.model.AlertTrigger

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(Array("classpath*:/applicationContext.xml"))
@ActiveProfiles(Array(TestProfile.PROFILE_NAME))
//TODO: use scalatest
class AlertServiceTest {

  @Inject var service: AlertService = _
  @Inject var userService: UserService = _

  @Test
  def test() {
    val user1 = userService.completeUserRegistration(null, null, null,  false)
    val user2 = userService.completeUserRegistration(null, null, null,  false)

    val alert1 = new Alert()
    alert1.name = "alert 1"
    alert1.keywords = Seq("word", "test 1")
    alert1.period = Daily.toString
    service.saveAlert(alert1, user1)

    val alert2 = new Alert()
    alert2.name = "alert 2"
    alert2.period = Daily.toString
    alert2.keywords = Seq("word", "test 2")
    service.saveAlert(alert2, user2)

    val all = ListBuffer[(AlertExecution, AlertTrigger)]()
    service.forAlertExecution(DateTime.now.plusDays(1).plusSeconds(10)) {
      (alert: AlertExecution, trigger: AlertTrigger) =>
        val e = (alert, trigger)
        all += e
    }
     
    Assert.assertEquals(2, all.size)
    val executionKeywords1 = all(0)._1.keywords.split(',')
    val executionKeywords2 = all(1)._1.keywords.split(',')
    Assert.assertEquals(2, executionKeywords1.size)
    Assert.assertTrue(executionKeywords1.contains("word"))
    Assert.assertTrue(executionKeywords1.contains("test 1"))
    Assert.assertEquals(2, executionKeywords2.size)
    Assert.assertTrue(executionKeywords2.contains("word"))
    Assert.assertTrue(executionKeywords2.contains("test 2"))

  }
}