package bg.statealerts.services.auth

import java.util.Set
import javax.inject.Inject
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.stereotype.Service
import bg.statealerts.dao.UserDao
import bg.statealerts.services.UserService
import java.util.ArrayList

@Service
class JpaUsersConnectionRepository extends UsersConnectionRepository {
    @Inject
    var userDao: UserDao = _

    @Inject
    var userService: UserService = _

    @Inject
    var locator: ConnectionFactoryLocator = _
    
    override def findUserIdsWithConnection(connection: Connection[_]): java.util.List[String] = {
        val auths = userDao.getSocialAuthentications(connection.getKey().getProviderId(), connection.getKey().getProviderUserId())
        var userIds = new ArrayList[String]()
        for (auth <- auths) {
            userIds.add(auth.user.id.toString)
        }
        return userIds
    }

    override def findUserIdsConnectedTo(providerId: String, providerUserIds: Set[String]): Set[String] = {
        return null
    }

    override def createConnectionRepository(userId: String): ConnectionRepository = {
        return new JpaConnectionRepository(userId.toLong, userService, userDao, locator)
    }
}
