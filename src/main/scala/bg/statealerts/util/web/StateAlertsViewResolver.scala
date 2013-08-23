package bg.statealerts.util.web

import org.fusesource.scalate.spring.view.ScalateViewResolver
import org.fusesource.scalate.servlet.Config
import org.fusesource.scalate.layout.DefaultLayoutStrategy
import org.fusesource.scalate.TemplateEngine
import scala.beans.BeanProperty

class StateAlertsViewResolver extends ScalateViewResolver {

  @BeanProperty
  var defaultLayoutsPrefix = "/WEB-INF/scalate/layouts/default."

  override def createTemplateEngine(config: Config) = {
    val engine = super.createTemplateEngine(config)
    engine.layoutStrategy = new DefaultLayoutStrategy(engine, TemplateEngine.templateTypes.map(defaultLayoutsPrefix + _):_*)
    engine
  }
}