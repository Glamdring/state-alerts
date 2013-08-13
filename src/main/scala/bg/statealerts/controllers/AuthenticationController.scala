package bg.statealerts.controllers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.social.connect.web.ProviderSignInController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.view.RedirectView
import bg.statealerts.services.UserService
import javax.inject.Inject
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.ui.Model
import org.springframework.social.connect.web.ProviderSignInAttempt
import org.springframework.social.twitter.api.TwitterProfile
import org.springframework.social.facebook.api.FacebookProfile
import bg.statealerts.model.User
import org.springframework.web.context.request.RequestAttributes
import org.apache.commons.lang3.StringUtils
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.twitter.api.Twitter
import org.hibernate.validator.internal.constraintvalidators.EmailValidator
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpSession
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Controller
class AuthenticationController {
    val logger: Logger = LoggerFactory.getLogger(classOf[AuthenticationController])

    @Inject
    var signInController: ProviderSignInController = _
    @Inject
    var signInAdapter: SocialSignInAdapter = _
    @Inject
    var userService: UserService = _
    @Inject
    var context: UserContext = _

    val restTemplate = {
    	val template = new RestTemplate()
        val formHttpMessageConverter = new FormHttpMessageConverter()
        val stringHttpMessageConverternew = new StringHttpMessageConverter()
        template.getMessageConverters().add(formHttpMessageConverter)
        template.getMessageConverters().add(stringHttpMessageConverternew)
        template
    }
	val emailValidator = new EmailValidator()
	
    @RequestMapping(value=Array("/signin/{providerId}"), method=Array(RequestMethod.GET), params=Array("home")) //param to discriminate from the cancellation url (ugly, I know)
    def signin(@PathVariable providerId: String, request: NativeWebRequest): RedirectView = {
        return signInController.signIn(providerId, request)
    }

    @RequestMapping(Array("/socialSignUp"))
    def socialSignupPage(@RequestParam(required=false) email: String, request: NativeWebRequest, model: Model): String = {
        val attempt = request.getAttribute(classOf[ProviderSignInAttempt].getName(), RequestAttributes.SCOPE_SESSION).asInstanceOf[ProviderSignInAttempt]
        if (attempt == null && StringUtils.isEmpty(email)) {
            return "redirect:/"
        }
        val user = new User()
        if (attempt != null) {
            val api = attempt.getConnection().getApi()
            if (api.isInstanceOf[Facebook]) {
                val profile = api.asInstanceOf[Facebook].userOperations().getUserProfile()
                user.email = profile.getEmail()
                user.names = profile.getName()
                user.username = profile.getUsername()
            } else if (api.isInstanceOf[Twitter]) {
                val profile = api.asInstanceOf[Twitter].userOperations().getUserProfile()
                user.names = profile.getName()
                user.username = profile.getScreenName()
            }
        } else {
            user.email = email
            model.addAttribute("user", user)
            model.addAttribute("type", "Persona")
        }
        model.addAttribute("user", user)
        return "socialSignup"
    }

    @RequestMapping(Array("/social/completeRegistration"))
    def completeRegistration(@RequestParam email: String, @RequestParam names: String,
            @RequestParam username: String, @RequestParam registrationType: String,
            @RequestParam(defaultValue = "false", required = false) loginAutomatically: Boolean,
            request: NativeWebRequest, model: Model): String = {

        if (!emailValidator.isValid(email, null)) {
            return "redirect:/?message=Invalid email. Try again"
        }

        val attempt = request.getAttribute(classOf[ProviderSignInAttempt].getName(), RequestAttributes.SCOPE_SESSION).asInstanceOf[ProviderSignInAttempt]
        if (attempt != null) {
            val user = userService.completeUserRegistration(email, username, names, attempt.getConnection(), loginAutomatically)
            signInAdapter.signIn(user, request.getNativeResponse().asInstanceOf[HttpServletResponse], true)
        } else if ("Persona".equals(registrationType)){
            val user = userService.completeUserRegistration(email, username, names, null, loginAutomatically)
            signInAdapter.signIn(user, request.getNativeResponse().asInstanceOf[HttpServletResponse], true)
        }
        // if the session has expired for a fb/tw registration, do not proceed - otherwise inconsistent data is stored
        return "redirect:/"
    }

    @RequestMapping(Array("/persona/auth"))
    @ResponseBody
    @throws[IOException]
    def authenticateWithPersona(@RequestParam assertion: String,
            @RequestParam userRequestedAuthentication: Boolean, request: HttpServletRequest,
            httpResponse: HttpServletResponse, model: Model): String = {
        if (context.user != null) {
            return ""
        }
        val params = new LinkedMultiValueMap[String, String]()
        params.add("assertion", assertion)
        params.add("audience", request.getScheme() + "://" + request.getServerName() + ":" + (if (request.getServerPort() == 80) "" else request.getServerPort()))
        val response = restTemplate.postForObject("https://verifier.login.persona.org/verify", params, classOf[PersonaVerificationResponse])
        if (response.status.equals("okay")) {
            val user = userService.getUserByEmail(response.email)
            if (user.isEmpty && userRequestedAuthentication) {
                return "/socialSignUp?email=" + response.email
            } else if (user.nonEmpty){
                if (userRequestedAuthentication || user.get.loginAutomatically) {
                    signInAdapter.signIn(user.get, httpResponse, true)
                    return "/"
                } else {
                    return ""
                }
            } else {
                return "" //in case this is not a user-requested operation, do nothing
            }
        } else {
            logger.warn("Persona authentication failed due to reason: " + response.reason)
            throw new IllegalStateException("Authentication failed")
        }
    }

    @RequestMapping(Array("/logout"))
    def logout(session: HttpSession, request: HttpServletRequest, response: HttpServletResponse): String = {
        session.invalidate()
        val cookie = WebUtils.getCookie(request, Constants.AuthTokenCookieName)
        if (cookie != null) {
            cookie.setMaxAge(0)
            cookie.setDomain(".computoser.com")
            cookie.setPath("/")
            response.addCookie(cookie)
        }

        val seriesCookie = WebUtils.getCookie(request, Constants.AuthTokenSeriesCookieName)
        if (seriesCookie != null) {
            seriesCookie.setMaxAge(0)
            seriesCookie.setDomain(".computoser.com")
            seriesCookie.setPath("/")
            response.addCookie(seriesCookie)
        }

        return "redirect:/"
    }
    @RequestMapping(Array("/digestEmailUnsubscribe/{id}"))
    def unsubscribe(@PathVariable id: Long, @RequestParam hash: String): String = {
        userService.unsubscribe(id, hash)
        return "redirect:/?message=Successfully unsubscribed"
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private case class PersonaVerificationResponse(
        status: String,
        email: String,
        reason: String){
    }
}
