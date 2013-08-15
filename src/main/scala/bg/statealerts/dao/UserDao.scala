package bg.statealerts.dao

import javax.persistence.Query
import javax.persistence.TypedQuery
import org.springframework.stereotype.Repository
import bg.statealerts.model.User
import bg.statealerts.model.SocialAuthentication
import scala.collection.JavaConversions

@Repository
class UserDao extends BaseDao {
	def getUserBySocialAuthentication(providerId: String, providerUserId: String): Option[User] = {
        var auths = getSocialAuthentications(providerId, providerUserId)
        if (auths.isEmpty) {
            return None
        } else {
            return Some(auths(0).user)
        }
    }

    def getSocialAuthentications(providerId: String, providerUserId: String): List[SocialAuthentication] = {
        val query = entityManager.createQuery("SELECT sa FROM SocialAuthentication sa WHERE sa.providerId=:providerId AND sa.providerUserId=:providerUserId", classOf[SocialAuthentication])
        query.setParameter("providerId", providerId)
        query.setParameter("providerUserId", providerUserId)

        var auths = query.getResultList()
        return JavaConversions.asScalaBuffer(auths).toList
    }

    def deleteSocialAuthentication(userId: Long, providerId: String) = {
        val query = entityManager.createQuery("DELETE FROM SocialAuthentication WHERE user.id=:userId AND providerId=:providerId")
        query.setParameter("userId", userId)
        query.setParameter("providerId", providerId)
        query.executeUpdate()
    }

    def getLoginFromAuthToken(token: String, series: String): Option[User] = {
      val details = new QueryDetails()
      details.query = "SELECT user FROM User user WHERE user.loginToken=:token AND user.loginSeries=:series"
      details.paramNames = Array("token", "series")
      details.paramValues = Array(token, series)
      val result = findByQuery(details)

      return getResult(result)
    }
    
}
