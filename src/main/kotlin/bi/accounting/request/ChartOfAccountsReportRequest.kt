package bi.accounting.request

import com.fasterxml.jackson.annotation.JsonTypeName
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate

@Serdeable
@Introspected
data class ChartOfAccountsReportRequest(
    var id: String? = null,
    var chunk: Int? = null,
    var isLastChunk: Boolean? = null,
    var date: LocalDate?,
    var timeframe: String?,
    var standardLayout: Boolean?,
    var trackingOptionID1: List<String>? = null,
    var trackingOptionID2: List<String>? = null,
    var periods: Int
){
    var batchId: String? = null
    var orgName: String? = null

    operator fun get(property: String): Any? {
        return when(property) {
            "id" -> id
            "chunk" -> chunk
            "isLastChunk" -> isLastChunk
            "date" -> date
            "timeframe" -> timeframe
            "periods" -> periods
            "standardLayout" -> standardLayout
            "trackingOptionID1" -> trackingOptionID1
            "trackingOptionID2" -> trackingOptionID2
            "orgName" -> orgName
            else -> throw IllegalArgumentException("Property $property not found")
        }
    }

    operator fun set(property: String, value: Any?) {
        when(property) {
            "id" -> id = value as? String
            "chunk" -> chunk = value as? Int
            "isLastChunk" -> isLastChunk = value as? Boolean
            "date" -> date = (value as? LocalDate)!!
            "timeframe" -> timeframe = (value as? String).toString()
            "periods" -> periods = (value as? Int)!!
            "standardLayout" -> standardLayout = (value as? Boolean)!!
            "trackingOptionID1" -> trackingOptionID1 = value as? List<String>
            "trackingOptionID2" -> trackingOptionID2 = value as? List<String>
            "orgName" -> orgName = value as? String
            else -> throw IllegalArgumentException("Property $property not found")
        }
    }
}