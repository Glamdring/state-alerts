package scalate

import org.fusesource.scalate.Binding
import org.fusesource.scalate.TemplateSource
import org.fusesource.scalate.support.TemplatePackage

class ScalatePackage extends TemplatePackage {
    def header(source: TemplateSource, bindings: List[Binding]) = """
      import bg.statealerts.model._
      import bg.statealerts.util.web.Messages
      import bg.statealerts.controllers.UserContext
      val msg = new Messages
      val root = if (request.getContextPath == "/") "" else request.getContextPath 
      val staticRoot = s"$root/static"
      val userLoggedIn = context.attribute("userContext").asInstanceOf[UserContext].user != null

      """
    
}