package bg.statealerts.controllers;

import javax.inject.Inject
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.joda.time.DateTimeConstants
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

    override def signIn(userId: String, connection: Connection[_], request: NativeWebRequest ): String = {
        val user = userService.getUser(userId.toLong);
        signIn(user, request.getNativeResponse(classOf[HttpServletRequest]), request.getNativeResponse(classOf[HttpServletResponse]), true);
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
        val authTokenCookie = new Cookie(Constants.AuthTokenCookieName, user.loginToken)
        authTokenCookie.setMaxAge(Constants.CookieAge)
        authTokenCookie.setPath(cookiePath(request))
        response.addCookie(authTokenCookie)

        val seriesCookie = new Cookie(Constants.AuthTokenSeriesCookieName, user.loginSeries)
        seriesCookie.setMaxAge(Constants.CookieAge)
        seriesCookie.setPath(cookiePath(request))
        response.addCookie(seriesCookie)
    }

    def removePermanentCookies(request: HttpServletRequest, response: HttpServletResponse)
    {
        val cookie = WebUtils.getCookie(request, Constants.AuthTokenCookieName)
        if (cookie != null) {
            cookie.setMaxAge(0)
            cookie.setPath(cookiePath(request))
            response.addCookie(cookie)
        }

        val seriesCookie = WebUtils.getCookie(request, Constants.AuthTokenSeriesCookieName)
        if (seriesCookie != null) {
            seriesCookie.setMaxAge(0)
            seriesCookie.setPath(cookiePath(request))
            response.addCookie(seriesCookie)
        }
    }

    def cookiePath(request: HttpServletRequest) = {
        // we currently need this cookie only for logout
        request.getContextPath + "/logout"
    }
}
