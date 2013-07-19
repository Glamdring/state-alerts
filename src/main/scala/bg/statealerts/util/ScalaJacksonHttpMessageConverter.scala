package bg.statealerts.util

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import com.fasterxml.jackson.databind.ObjectMapper

class ScalaJacksonHttpMessageConverter(objectMapper: ObjectMapper) extends MappingJackson2HttpMessageConverter {
  setObjectMapper(objectMapper)
}