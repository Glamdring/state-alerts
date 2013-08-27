package bg.statealerts.controllers;

import org.hibernate.engine.jdbc.SerializableBlobProxy
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext
import bg.statealerts.model.User;
import scala.beans.BeanProperty

@Component
@Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode=ScopedProxyMode.TARGET_CLASS)
class UserContext extends Serializable {

  @BeanProperty
  var user: User = _
}
