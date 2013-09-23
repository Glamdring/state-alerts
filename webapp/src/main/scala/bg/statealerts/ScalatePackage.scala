package bg.statealerts

import org.fusesource.scalate.Binding
import org.fusesource.scalate.TemplateSource
import org.fusesource.scalate.support.TemplatePackage

class ScalatePackage extends TemplatePackage {
    def header(source: TemplateSource, bindings: List[Binding]) = """
      import bg.statealerts.model._
      import bg.statealerts.views._
      import bg.statealerts.controllers.UserContext
      import bg.statealerts.controllers.GoogleAnalyticsConfig

      val implicits = new Implicits
      import implicits._
      val root = if (request.getContextPath == "/") "" else request.getContextPath 
      val staticRoot = s"$root/static"
      val userLoggedIn = context.attribute[UserContext](USER_CONTEXT_ATTRIBUTE_NAME).user != null

      """

}