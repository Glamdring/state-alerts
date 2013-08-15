package bg.statealerts.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import com.codahale.jerkson.Json
import org.springframework.http.MediaType

class ScalaJsonHttpMessageConverter extends AbstractHttpMessageConverter[Object] {
  
  def supports(x: Class[_]): Boolean = {
    true
  }
  
  override def canRead(mediaType: MediaType): Boolean = {
    return mediaType != null && mediaType.getSubtype() != null && mediaType.getSubtype().contains("json")
  }
  
  override def readInternal(clazz: Class[_ <: Object], input: HttpInputMessage): Object = {
    Json.parse(input.getBody())
  }
  
  override def writeInternal(obj: Object, output: HttpOutputMessage): Unit = {
    Json.generate(obj, output.getBody())
  }
  
}