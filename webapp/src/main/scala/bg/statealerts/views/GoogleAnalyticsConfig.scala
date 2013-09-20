package bg.statealerts.views

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import scala.beans.BeanProperty

@Component
class GoogleAnalyticsConfig {

	@Value("${ga.tickerId:}")
	@BeanProperty
	var tickerId: String = _

	@Value("${ga.configObject:}")
	var configObject: String = _

	@BeanProperty
	lazy val maybeConfigObject: String = Option(configObject).getOrElse("{}")
}
