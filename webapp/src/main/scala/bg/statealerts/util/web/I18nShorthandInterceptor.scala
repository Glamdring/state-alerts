package bg.statealerts.util.web

import org.springframework.context.MessageSource
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import org.springframework.web.servlet.support.RequestContextUtils

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class I18nShorthandInterceptor extends HandlerInterceptorAdapter {

  @Inject
  var messageSource: MessageSource = _

  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Object): Boolean = {
    request.setAttribute(MSG_ATTRIBUTE_NAME, new DelegatingI18nMap(messageSource, RequestContextUtils.getLocale(request)))
    true
  }
}

