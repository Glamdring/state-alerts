package bg.statealerts.services

import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import javax.inject.Inject
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.social.connect.Connection
import org.hibernate.StaleStateException
import bg.statealerts.model.SocialAuthentication
import bg.statealerts.dao.UserDao
import bg.statealerts.model.User
import java.util.UUID
import org.joda.time.DateTime
import bg.statealerts.util.SecurityUtils
import org.joda.time.Period
import bg.statealerts.services.auth.AuthUtils
import bg.statealerts.model.SocialAuthentication

@Service
class UserService {
  
	val logger: Logger = LoggerFactory.getLogger(classOf[UserService])

    @Inject
    var userDao: UserDao = _

    @Value("${hmac.key}")
    var hmacKey: String = _
    
    @Transactional
    def connect(userId: Long, auth: SocialAuthentication) = {
        val existingAuths: List[SocialAuthentication]  = userDao.getSocialAuthentications(auth.providerId, auth.providerUserId)

        if (existingAuths.isEmpty) {
            val user = userDao.get(classOf[User], userId)
            auth.user = user
            userDao.save(auth)
        } else {
            val existingAuth = existingAuths(0)
            existingAuth.expirationTime = auth.expirationTime
            existingAuth.refreshToken = auth.refreshToken
            existingAuth.imageUrl = auth.imageUrl
            userDao.save(existingAuth)
        }
    }

    @Transactional
    def deleteSocialAuthentication(userId: Long, providerId: String) = {
        userDao.deleteSocialAuthentication(userId, providerId)
    }

    @Transactional(readOnly=true)
    def getUser(id: Long): User = {
        userDao.get(classOf[User], id)
    }

    @Transactional(readOnly=true)
    def getUserByEmail(email: String): Option[User] = {
        return userDao.getByPropertyValue(classOf[User], "email", email)
    }

    @Transactional
    def completeUserRegistration(email: String, names: String, connection: Connection[_], loginAutomatically: Boolean): User = {
        var user = new User()
        user.email = email
        user.names = names
        user.loginAutomatically = loginAutomatically
        user.registrationTime = new DateTime()
        user.receiveEmails = true
        user = userDao.save(user)
        if (connection != null) {
            import AuthUtils.connectionToAuth
            val auth: SocialAuthentication = connection
            auth.user = user
            userDao.save(auth)
        }
        return user
    }

    @Transactional
    def unsubscribe(id: Long, hash: String) = {
        val user = userDao.get(classOf[User], id)
        if (hash.equals(SecurityUtils.hmac(user.email, hmacKey))) {
            user.receiveEmails = false
            userDao.save(user)
        }
    }

    /**
     * http://jaspan.com/improved_persistent_login_cookie_best_practice
     */
    @Transactional//(rollbackFor=classOf[StaleStateException])
    def rememberMeLogin(token: String , series: String): Option[User] = {
        val existingLogin = userDao.getLoginFromAuthToken(token, series)
        if (existingLogin.isEmpty) {
            val loginBySeries = userDao.getByPropertyValue(classOf[User], "loginSeries", series)
            // if a login series exists, assume the previous token was stolen, so deleting all persistent logins.
            // An exception is a request made within a few seconds from the last login time
            // which may mean request from the same browser that is not yet aware of the renewed cookie
            loginBySeries.foreach(l => {
              if (new Period(l.lastLoginTime, new DateTime()).getSeconds() < 5) {
                logger.info("Assuming login cookies theft deleting all sessions for user " + loginBySeries)
                l.loginSeries = null
                l.loginToken = null
                userDao.save(loginBySeries)
              } else if (logger.isDebugEnabled()) {
                logger.debug("No existing login found for token=" + token + ", series=" + series)
              }
            })
            if (loginBySeries.isEmpty) {
              logger.debug("No existing login found for token=" + token + ", series=" + series)
            }
            return None
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Existing login found for token=" + token + " and series=" + series)
        }
        val existing = existingLogin.get
        fillUserWithNewTokens(existing, series)
        return Some(existing)
    }

    @Transactional
    def fillUserWithNewTokens(user: User, series: String) = {
        user.loginToken = UUID.randomUUID().toString()
        user.loginSeries = if (series != null) series else UUID.randomUUID().toString()
        user.lastLoginTime = new DateTime()

        userDao.save(user)
    }
    
    @Transactional(readOnly=true)
    def canUseApi(token: String): Boolean = {
    val user = userDao.getByPropertyValue(classOf[User], "token", token);
    return user.nonEmpty && user.get.corporate == true;
  }
}