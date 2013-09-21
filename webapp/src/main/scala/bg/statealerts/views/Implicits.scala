package bg.statealerts.views

import org.fusesource.scalate.servlet.ServletRenderContext._
import org.fusesource.scalate.servlet.ServletRenderContext

class Implicits(implicit context: ServletRenderContext) {
  val msg = new Messages(context.attributes(MSG_ATTRIBUTE_NAME).asInstanceOf[java.util.Map[String, Any]])
}

class Messages(private val messages: java.util.Map[String, Any]) {

    def apply(key: String): Any = messages.get(key)
}