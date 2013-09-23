package bg.statealerts.controllers;

import javax.inject.Inject
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.joda.time.DateTimeConstants
import org.springframework.beans.factory.annotation.Value
import org.springframework.social.connect.Connection
import org.springframework.social.connect.web.SignInAdapter
import org.springframework.stereotype.Component
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.util.WebUtils
import bg.statealerts.services.UserService
import bg.statealerts.model.User


@Component
class SocialSignInAdapter extends SignInAdapter {

    @Inject
    var context: UserContext = _
    @Inject
    var userService: UserService = _
    @Value("${ui.cookieDomain:}")
    var cookieDomain: String = _

    override def signIn(userId: String, connection: Connection[_], request: NativeWebRequest ): String = {
        val user = userService.getUser(userId.toLong);
        signIn(user, request.getNativeRequest(classOf[HttpServletRequest]), request.getNativeResponse(classOf[HttpServletResponse]), true);
        return "/";
    }

    def signIn(user: User, request: HttpServletRequest, response: HttpServletResponse, resetTokens: Boolean) = {
        context.user = user;
        if (resetTokens) {
            userService.fillUserWithNewTokens(user, null);
        }
        addPermanentCookies(user, request, response)
    }

    def signOut(request: HttpServletRequest, response: HttpServletResponse) = {
        removePermanentCookies(request, response)
    }

    def addPermanentCookies(user: User, request: HttpServletRequest, response: HttpServletResponse) = {
        addCookie(request, response, Constants.AuthTokenCookieName, user.loginToken)
        addCookie(request, response, Constants.AuthTokenSeriesCookieName, user.loginSeries)
    }

    def removePermanentCookies(request: HttpServletRequest, response: HttpServletResponse)
    {
        removeCookie(request, response, Constants.AuthTokenCookieName)
        removeCookie(request, response, Constants.AuthTokenSeriesCookieName)
    }

    private def addCookie(request: HttpServletRequest, response: HttpServletResponse, name: String, value: String) {
        val cookie = new Cookie(name, value)
        cookie.setMaxAge(Constants.CookieAge)
        if (cookieDomain != null && !cookieDomain.isEmpty())
        {
            cookie.setDomain(cookieDomain)
        }
        cookie.setPath(cookiePath(request))
        response.addCookie(cookie)
    }

    private def removeCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
        val cookie = WebUtils.getCookie(request, name)
        if (cookie != null) {
            cookie.setMaxAge(0)
            if (cookieDomain != null && !cookieDomain.isEmpty())
            {
                cookie.setDomain(cookieDomain)
            }
            cookie.setPath(cookiePath(request))
            response.addCookie(cookie)
        }
    }

    private def cookiePath(request: HttpServletRequest) = {
        // we currently need this cookie only for logout
        request.getContextPath + "/logout"
    }
}
