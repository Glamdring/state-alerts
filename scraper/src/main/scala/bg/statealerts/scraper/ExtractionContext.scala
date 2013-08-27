package bg.statealerts.scraper

import bg.statealerts.scraper.config.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.WebClient

class ExtractionContext(
    val descriptor: ExtractorDescriptor, 
    val baseUrl: String, 
    val dateFormatter: DateTimeFormatter,
    val client: WebClient) {
}