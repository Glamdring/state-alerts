package bg.statealerts.services.auth

import org.springframework.social.connect.ConnectionData
import bg.statealerts.model.SocialAuthentication
import org.springframework.social.connect.Connection

object AuthUtils {

  implicit def connectionToAuth(connection: Connection[_]): SocialAuthentication = {
        val auth = new SocialAuthentication()
        val data = connection.createData()
        auth.providerId = data.getProviderId()
        auth.token = data.getAccessToken()
        auth.refreshToken = data.getRefreshToken()
        auth.secret = data.getSecret()
        auth.providerUserId = data.getProviderUserId()
        return auth
    }
}