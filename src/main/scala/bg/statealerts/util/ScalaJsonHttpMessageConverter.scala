package bg.statealerts.util

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import com.codahale.jerkson.Json

class ScalaJsonHttpMessageConverter extends AbstractHttpMessageConverter[Object] {
  
  def supports(x: Class[_]): Boolean = {
    true
  }
  
  def readInternal[T](clazz: Class[T], input: HttpInputMessage): T = {
    Json.parse(input.getBody())
  }
  
  def writeInternal[T](obj: T, output: HttpOutputMessage) = {
    Json.generate(obj, output.getBody())
  }
  
}