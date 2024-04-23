package bi.accounting.service

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ReportRequest(
    @JsonProperty("Org-IDs")
    val orgIds: List<String>,
    @JsonProperty("Request-Body")
    val requestBody: String,
    @JsonProperty("User-ID")
    val userId: String,
    @JsonProperty("Report-Type")
    val reportType: String,
){
    var id: String? = null
    var orgName: String? = null
}