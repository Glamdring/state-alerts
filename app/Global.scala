import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import play.Application
import play.GlobalSettings

object Global extends GlobalSettings {

    var ctx: ApplicationContext = null;

    override def onStart(app:Application) {
    	ctx = new ClassPathXmlApplicationContext("components.xml");
    }

    override def getControllerInstance[A](clazz: Class[A]) : A = {
    	return ctx.getBean(clazz);
    }
}