import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter

class MapConverter : AttributeConverter<Map<String, Any>, String> {

    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, Any>?): String {
        return objectMapper.writeValueAsString(attribute ?: emptyMap<String, Any>())
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any> {
        return objectMapper.readValue(dbData ?: "{}")
    }
}

