package bi.accounting

import bi.accounting.service.ReportRequest
import bi.accounting.service.ReportService
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import io.micronaut.context.annotation.Value
import io.micronaut.function.FunctionBean
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import java.util.function.Consumer

@FunctionBean("abi-report-functions-zoho")
class SqsEventConsumer: Consumer<SQSEvent> {
    @Inject
    lateinit var reportServiceMap: Map<String, ReportService>

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Value("\${source.arn}")
    var sourceArn: String = ""

    override fun accept(event: SQSEvent) {
        event.records.forEach { record ->
            val body = record.body
            LOG.info("SQS event received: {} from {}", body.toString(), record.eventSourceArn)
            if(record.eventSourceArn != sourceArn) {
                LOG.error("Event source ARN does not match expected ARN {}", sourceArn)
                return
            }
            val request = objectMapper.readValue(body, ReportRequest::class.java)
            val service = reportServiceMap[request.reportType] ?: throw IllegalArgumentException("Invalid report type: ${request.reportType}")
            service.generateReport(request)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FunctionRequestHandler::class.java)
    }
}