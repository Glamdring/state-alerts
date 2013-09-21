package bg.statealerts.util.web

import javax.servlet.http.HttpServletRequest
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.util.Locale
import org.springframework.context.MessageSource
import org.springframework.beans.factory.annotation.Value
import bg.statealerts.controllers.UserContext
import bg.statealerts.views._

class RequestScopedDataSettingInterceptor extends HandlerInterceptorAdapter {

  @Inject
  var userContext: UserContext = _

  // TODO: this should be actually application scoped.
  @Inject
  var ga: GoogleAnalyticsConfig = _

  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Object): Boolean = {
    request.setAttribute(USER_CONTEXT_ATTRIBUTE_NAME, userContext)
    request.setAttribute(GA_ATTRIBUTE_NAME, ga)
    true
  }
}