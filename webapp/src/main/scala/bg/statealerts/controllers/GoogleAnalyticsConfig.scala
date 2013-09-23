package bg.statealerts.controllers

import org.springframework.beans.factory.annotation.Value
import scala.beans.BeanProperty
import org.springframework.stereotype.Controller

@Controller
class GoogleAnalyticsConfig {

  @Value("${ga.tickerId:}")
  @BeanProperty
  var tickerId: String = _

  @Value("${ga.configObject:}")
  var configObject: String = _

  @BeanProperty
  lazy val maybeConfigObject: String = Option(configObject).getOrElse("{}")
}
