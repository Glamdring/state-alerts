package bg.statealerts.util

import java.lang.reflect.Method
import org.springframework.cache.interceptor.KeyGenerator
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.apache.commons.lang3.ClassUtils
import org.springframework.expression.spel.support.ReflectivePropertyAccessor.CacheKey
import scala.Enumeration
import scala.Enumeration

/**
 * Copied from DefaultKeyGenerator, but using the class method names in the key as well
 *
 * @author bozho
 *
 */
class CacheKeyGenerator extends KeyGenerator {
	//val logger: Logger = LoggerFactory.getLogger(classOf[DSPCacheKeyGenerator]);

	val NoParamKey: Int = 0;
	val NullParamKey: Int = 53;

	def generate(target: Any, method: Method, params: Object*): Object = {
		var key: StringBuilder = new StringBuilder();
		key.append(target.getClass().getSimpleName()).append(".").append(method.getName()).append(":");

		if (params.length == 0) {
			return key.append(NoParamKey).toString();
		}

		for (param <- params) {
			if (param == null) {
				key.append(NullParamKey);
			} else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param.isInstanceOf[String]) {
				key.append(param);
			} else if (param.getClass().isEnum()) {
				key.append(param.toString());
			} else {
				//logger.warn("Using an object as a cache key may lead to unexpected results. "
				//		+ "Either use @Cacheable(key=..) or implement CacheKey. Method is " + target.getClass() + "#"
				//		+ method.getName());
				key.append(param.hashCode());
			}
		}

		return key.toString();
	}
}
