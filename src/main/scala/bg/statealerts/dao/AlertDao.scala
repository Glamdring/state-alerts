package bg.statealerts.dao

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import bg.statealerts.model.Alert

trait AlertDao extends BaseDao {
  def getAlerts(): Seq[Alert]

}


@Repository("alertDao")
class HibernateAlertDao extends HibernateScrolling with AlertDao {

  @Value("1000")
  var fetchSize: Int = _

  def getAlerts(): Stream[Alert] = stream(session => {
    import org.hibernate.LockMode
    import org.hibernate.ScrollMode

    val query =
      session
        .createQuery("from Alert order by email, name, id")
        .setFetchSize(fetchSize)
        .setReadOnly(true)
        .setLockMode("a", LockMode.NONE)

    query.scroll(ScrollMode.FORWARD_ONLY)
  })

}
