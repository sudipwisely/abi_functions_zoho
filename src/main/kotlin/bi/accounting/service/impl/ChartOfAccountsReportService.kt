package bi.accounting.service.impl

import bi.accounting.CompressionUtil
import bi.accounting.InQueueProducer
import bi.accounting.RetryQueueProducer
import bi.accounting.client.AccountServiceClient
import bi.accounting.client.ZohoApiClient
import bi.accounting.repository.RequestRepository
import bi.accounting.service.ReportRequest
import bi.accounting.service.ReportService
import bi.accounting.request.ChartOfAccountsReportRequest
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.LocalDate

@Named("chartofaccounts")
@Singleton
class ChartOfAccountsReportService(
    private val objectMapper: ObjectMapper,
    private val zohoApiClient: ZohoApiClient,
    inQueueProducer: InQueueProducer,
    compressionUtil: CompressionUtil,
    accountServiceClient: AccountServiceClient,
    requestRepository: RequestRepository,
    retryQueueProducer: RetryQueueProducer
): BaseService(objectMapper, compressionUtil, inQueueProducer, accountServiceClient,requestRepository, retryQueueProducer) {

    override fun getReport(type: String, token: String, orgId: String, userId: String, requestBody: String, orgName: String){
        val requestMap = objectMapper.readValue(requestBody, ChartOfAccountsReportRequest::class.java)
        requestMap.orgName = orgName
        requestMap.batchId = ObjectIdGenerators.UUIDGenerator().generateId(requestBody).toString()
        zohoApiClient.getReportChartofAccouts("Zoho-oauthtoken $token", orgId)
            .doOnError { error ->
                handleException(error, requestBody)
            }
            .map { response ->
                LOG.info(response.toString())
                sendReportToInQueue(type, response, userId, orgId, objectMapper.writeValueAsString(requestMap))
            }
            .block()
    }

    private fun trackingOptionFrom(id: String, requestMap: ChartOfAccountsReportRequest): String {
        val options = requestMap[id] as List<*>?
        if(options === null || options.isEmpty()){
            return ""
        }
        val t = objectMapper.readValue(options[0].toString(), HashMap::class.java)
        LOG.info("trackingOptionFrom {}", t)
        requestMap[id] = listOf(t["name"].toString())
        return t["id"].toString()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ChartOfAccountsReportRequest::class.java)
    }
}