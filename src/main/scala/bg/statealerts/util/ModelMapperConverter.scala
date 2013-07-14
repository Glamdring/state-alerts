package bg.statealerts.util

import java.util.Collections
import java.util.Set
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair

class ModelMapperConverter extends GenericConverter with InitializingBean {

	var mapper: ModelMapper = new ModelMapper();
	
	def afterPropertiesSet() = {
		// use mapper.getConfiguration() to setup ModelMapper
	}
	
	def getConvertibleTypes(): Set[ConvertiblePair] = {
		return Collections.singleton(new ConvertiblePair(classOf[Any], classOf[Any]));
	}

	def convert(source: Any, sourceType: TypeDescriptor, targetType: TypeDescriptor): Object = {
		return mapper.map(source, targetType.getObjectType())
	}
}
