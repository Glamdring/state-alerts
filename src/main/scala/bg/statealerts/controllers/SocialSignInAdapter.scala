package bg.statealerts.controllers;

import javax.inject.Inject
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import org.joda.time.DateTimeConstants
import org.springframework.social.connect.Connection
import org.springframework.social.connect.web.SignInAdapter
import org.springframework.stereotype.Component
import org.springframework.web.context.request.NativeWebRequest
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
        signIn(user, request.getNativeResponse().asInstanceOf[HttpServletResponse], true);
        return "/";
    }

    def signIn(user: User , response: HttpServletResponse, resetTokens: Boolean) = {
        context.user = user;
        if (resetTokens) {
            userService.fillUserWithNewTokens(user, null);
        }
        addPermanentCookies(user, response);
    }

    def addPermanentCookies(user: User, response: HttpServletResponse) = {
        val authTokenCookie = new Cookie(Constants.AuthTokenCookieName, user.loginToken);
        authTokenCookie.setMaxAge(Constants.CookieAge);
        authTokenCookie.setPath("/");
        authTokenCookie.setDomain(".statealerts.com");
        response.addCookie(authTokenCookie);

        val seriesCookie = new Cookie(Constants.AuthTokenSeriesCookieName, user.loginSeries);
        seriesCookie.setMaxAge(Constants.CookieAge);
        seriesCookie.setPath("/");
        seriesCookie.setDomain(".statealerts.com");
        response.addCookie(seriesCookie);
    }
}
