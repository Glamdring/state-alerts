package bg.statealerts.util

import com.fasterxml.jackson.databind.JsonDeserializer
import org.joda.time.DateTime
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.JsonSerializer

class LongDateTimeSerializer extends JsonSerializer[DateTime] {

    def serialize(value: DateTime , gen: JsonGenerator, provider: SerializerProvider) = {
        gen.writeNumber(value.getMillis());
    }
}