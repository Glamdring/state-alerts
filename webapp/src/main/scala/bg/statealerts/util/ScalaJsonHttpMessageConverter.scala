package bg.statealerts.util

import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.databind.SerializationFeature

class ScalaJsonHttpMessageConverter extends AbstractHttpMessageConverter[Object] {
  
  val mapper: ObjectMapper = createMapper();
 
  def createMapper() : ObjectMapper = {
    val mapper = new ObjectMapper()
    mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.registerModule(DefaultScalaModule)
    mapper.registerModule(new JodaModule())
    mapper
  }
  
  def supports(x: Class[_]): Boolean = {
    true
  }
  
  override def canRead(mediaType: MediaType): Boolean = {
    return mediaType != null && mediaType.getSubtype() != null && mediaType.getSubtype().contains("json")
  }
  
  override def readInternal(clazz: Class[_ <: Object], input: HttpInputMessage): Object = {
    mapper.readValue(input.getBody(), clazz);
  }
  
  override def writeInternal(obj: Object, output: HttpOutputMessage): Unit = {
    mapper.writeValue(output.getBody(), obj);
  }
  
}