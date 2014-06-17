package bg.statealerts.util

import javax.annotation.PostConstruct
import org.joda.time.DateTimeZone
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import bg.statealerts.scraper.config.ExtractorConfiguration
import bg.statealerts.scraper.Extractor
import org.joda.time.DateTime

object ManualExtractor {
  def main(args: Array[String]) {
    if (args.length != 3) {
      println("Parameters: <path-to-config> <source-key> <out-file>")
      return
    }
    val path = args(0)
    
    val mapper: ObjectMapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    DateTimeZone.setDefault(DateTimeZone.UTC)
    val file = new File(path + "/extractors.json")
    val config = mapper.readValue(file, classOf[ExtractorConfiguration])
    for (descriptor <- config.extractors) {
      var extractor = new Extractor(descriptor)
      if (extractor.descriptor.sourceKey == args(1)) {
        val result = extractor.extract(new DateTime().minusYears(2))
        mapper.writeValue(new File(args(2)), result);
      }
    }
  }
}