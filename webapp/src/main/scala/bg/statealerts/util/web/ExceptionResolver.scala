package bg.statealerts.util.web

import org.springframework.web.servlet.HandlerExceptionResolver
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.web.servlet.ModelAndView
import org.springframework.stereotype.Component

@Component
class ExceptionResolver extends HandlerExceptionResolver {

  def logger = LoggerFactory.getLogger(classOf[ExceptionResolver]);
  
  def resolveException(request: HttpServletRequest, response: HttpServletResponse, handler: Object, ex: Exception): ModelAndView = {
		// the stacktrace will be printed by spring's DispatcherServlet
        // we are only logging the request url and headeres here
        logger.warn("An exception occurred when invoking the following URL: "
                + request.getRequestURL() + " . Requester IP is "
                + request.getRemoteAddr() + ", User-Agent: "
                + request.getHeader("User-Agent") + "; Message=" + ex.getMessage() + ": " + ex.getStackTrace()(0).getMethodName())

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        null;
  }
}