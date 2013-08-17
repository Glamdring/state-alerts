package bg.statealerts.util.web

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import javax.annotation.PostConstruct
import java.util.Locale

class I18nShorthandInterceptor extends HandlerInterceptorAdapter {

    @Inject
    var messageSource: MessageSource = _

    @Value("${ui.locale}")
    var configuredLocale: String = _
    
    var locale: Locale = _
    
    @PostConstruct
    def init() = {
      locale = new Locale(configuredLocale) 
    }
    
    override def preHandle(request: HttpServletRequest,response: HttpServletResponse, handler: Object ): Boolean = {
        request.setAttribute("msg", new DelegatingI18nMap(messageSource, locale))
        return true;
    }
}
