package bg.statealerts.services.auth

package com.music.service.auth

import java.util.List
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionData
import org.springframework.social.connect.ConnectionFactory
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.ConnectionKey
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.NoSuchConnectionException
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.MultiValueMap
import bg.statealerts.services.UserService
import bg.statealerts.dao.UserDao
import bg.statealerts.model.SocialAuthentication

class JpaConnectionRepository (userId: Long, userService: UserService, userDao: UserDao, locator: ConnectionFactoryLocator) extends ConnectionRepository {

    override def findAllConnections(): MultiValueMap[String, Connection[_]] = {
        return null
    }

    override def findConnections(providerId: String): List[Connection[_]] = {
        return null
    }

    override def findConnections[A](apiType: Class[A]): List[Connection[A]] = {
        return null
    }

    override def findConnectionsToUsers(providerUserIds: MultiValueMap[String, String]): MultiValueMap[String, Connection[_]] = {
        return null
    }

    override def getConnection(connectionKey: ConnectionKey): Connection[_] = {
        return getConnection(connectionKey.getProviderId(), connectionKey.getProviderUserId())
    }

    override def getConnection[A](apiType: Class[A], providerUserId: String): Connection[A] = {
        val providerId = locator.getConnectionFactory(apiType).getProviderId()
        return getConnection(providerId, providerUserId).asInstanceOf[Connection[A]]
    }

    def getConnection(providerId: String, providerUserId: String): Connection[_] = {
        val socialAuthentications = userDao.getSocialAuthentications(providerId, providerUserId)
        if (socialAuthentications.isEmpty) {
            throw new NoSuchConnectionException(new ConnectionKey(providerId, providerUserId))
        }
        return authToConnection(socialAuthentications(0))
    }

    override def getPrimaryConnection[A](apiType: Class[A]): Connection[A] = {
        return null
    }

    override def findPrimaryConnection[A](apiType: Class[A]): Connection[A] = {
        return null
    }

    @Transactional
    override def addConnection(connection: Connection[_]) = {
        val auth = AuthUtils.connectionToAuth(connection)
        userService.connect(userId, auth)
    }

    override def updateConnection(connection: Connection[_]) = {
        val auth = AuthUtils.connectionToAuth(connection)
        userService.connect(userId, auth)
    }

    def authToConnection(auth: SocialAuthentication): Connection[_] = {
        val connectionFactory = locator.getConnectionFactory(auth.providerId)
        val data = new ConnectionData(auth.providerId, auth.providerUserId, null, null,
                auth.imageUrl, auth.token, auth.secret, auth.refreshToken,
                auth.expirationTime)
        return connectionFactory.createConnection(data)
    }

    override def removeConnections(providerId: String) = {
        userService.deleteSocialAuthentication(userId, providerId)
    }

    override def removeConnection(connectionKey: ConnectionKey) = {
        userService.deleteSocialAuthentication(userId, connectionKey.getProviderId())
    }

}
