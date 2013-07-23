package bg.statealerts.services.extractors

import bg.statealerts.scheduled.ExtractorDescriptor
import org.joda.time.format.DateTimeFormatter
import com.gargoylesoftware.htmlunit.WebClient

class ExtractionContext(
    val descriptor: ExtractorDescriptor, 
    val baseUrl: String, 
    val dateFormatter: DateTimeFormatter,
    val client: WebClient) {
}