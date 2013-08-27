package bg.statealerts.util.web

import org.fusesource.scalate.servlet.ServletRenderContext._
import org.fusesource.scalate.servlet.ServletRenderContext

class Messages(implicit context: ServletRenderContext) {
  val messages =  context.attributes(MSG_ATTRIBUTE_NAME).asInstanceOf[java.util.Map[String, Any]]

  def apply(key: String): Any = messages.get(key)
}
