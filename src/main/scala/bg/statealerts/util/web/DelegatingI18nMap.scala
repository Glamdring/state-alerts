package bg.statealerts.util.web

import java.util.Locale
import org.springframework.context.MessageSource
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.context.NoSuchMessageException
import java.util.HashMap
import com.google.common.collect.ForwardingMap

class DelegatingI18nMap(messageSource: MessageSource, locale: Locale) extends ForwardingMap[String, String] {
  private val logger: Logger = LoggerFactory.getLogger(classOf[DelegatingI18nMap]);

  override def get(key: Any): String = {
    try {
      return messageSource.getMessage(key.asInstanceOf[String], null, locale);
    } catch {
      case ex: NoSuchMessageException => {
        if (!locale.equals(Locale.getDefault())) {
          try {
            return messageSource.getMessage(key.asInstanceOf[String], null, Locale.getDefault());
          } catch {
            case e: NoSuchMessageException => {
              logger.warn(e.getMessage());
              return key.asInstanceOf[String];
            }
          }
        } else {
          logger.warn(ex.getMessage());
          return key.asInstanceOf[String];
        }
      }
    }
  }

  /**
   * method used for parsing messages with parameter placeholders
   * @param key
   * @param args
   * @return
   */
  	def get(key: String, args: String*): String = {
    try {
      return messageSource.getMessage(key, args.toArray, locale);
    } catch {
      case ex: NoSuchMessageException => {
        if (!locale.equals(Locale.getDefault())) {
          try {
            return messageSource.getMessage(key.asInstanceOf[String], args.toArray, Locale.getDefault());
          } catch {
            case e: NoSuchMessageException => {
              logger.warn(e.getMessage());
              return key;
            }
          }
        } else {
          logger.warn(ex.getMessage());
          return key;
        }
      }
    }
  }

  override def delegate(): java.util.Map[String, String] = {
    // no need to implement this, the forwarding map is used
    // as a simple adapter with empty implementations
    // calling them would result in NPE, but they are never called
    // because this class is used only in JSTL expressions like
    // ${mapAttr.key}
    return null;
  }
}