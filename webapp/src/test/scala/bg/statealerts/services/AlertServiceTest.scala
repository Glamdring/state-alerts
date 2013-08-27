package bg.statealerts.services

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.Assert
import org.springframework.test.context.ActiveProfiles
import bg.statealerts.util.TestProfile

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(Array("classpath*:/applicationContext.xml"))
@ActiveProfiles(Array(TestProfile.PROFILE_NAME))
class AlertServiceTest {

  @Autowired var service: AlertService = _

  @Test
  def test() {
    Assert.assertTrue(true)
  }
}