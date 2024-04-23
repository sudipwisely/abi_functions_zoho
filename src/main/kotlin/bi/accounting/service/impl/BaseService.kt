package bi.accounting.service.impl

import bi.accounting.CompressionUtil
import bi.accounting.InQueueProducer
import bi.accounting.RetryQueueProducer
import bi.accounting.client.AccountServiceClient
import bi.accounting.repository.RequestRepository
import bi.accounting.service.ReportRequest
import bi.accounting.service.ReportService
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.serde.ObjectMapper
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.IOException


open class BaseService(
    private val objectMapper: ObjectMapper,
    private val compressionUtil: CompressionUtil,
    private val inQueueProducer: InQueueProducer,
    private val accountServiceClient: AccountServiceClient,
    private val requestRepository: RequestRepository,
    private val retryQueueProducer: RetryQueueProducer
): ReportService {

    override fun generateReport(reportRequest: ReportRequest) {
        LOG.info("generate report enter");
        val requestBody = objectMapper.readValue(reportRequest.requestBody, Map::class.java)
        val requestId = requestBody["id"].toString()
        val type = reportRequest.reportType
        val userId = reportRequest.userId.toLong()
        val orgIds = reportRequest.orgIds
        val reportRequestBody = reportRequest.requestBody



        requestRepository.saveRequestData(requestId, userId, orgIds.joinToString(","), orgIds.size,
            reportRequestBody
        )

        orgIds.forEach {
            val orgId = it
            fetchAccountToken(userId, orgId)
                .subscribeOn(Schedulers.boundedElastic())
                .map { account ->
                    val accountData = objectMapper.readValue(account, Map::class.java)
                    val token = accountData["accessToken"].toString()
                    val name = accountData["orgName"].toString()
                    LOG.info("My token")
                    LOG.info(token)
                    getReport(type, token, orgId, userId.toString(), reportRequestBody, name)
                }
                .block()
        }
    }

    private fun fetchAccountToken(userId: Long, orgId: String): Mono<String> {
        LOG.info("Retrieving account access token for user $userId and org $orgId")
        return accountServiceClient.getAccountToken(userId.toString(), orgId)
            .doOnError { LOG.error("Error getting account", it) }
    }

    open fun getReport(type: String, token: String, orgId: String, userId: String, requestBody: String, orgName: String) {
        LOG.info("Getting report not implemented")
        return
    }

    fun sendReportToInQueue(type: String, response: String?, userId: String, orgId: String, requestBody: String) {
        val reportResponse = HashMap<String, String>()
        reportResponse["Report-Type"] = type
        reportResponse["Response-Body"] = response!!
        reportResponse["User-ID"] = userId
        reportResponse["Org-ID"] = orgId
        reportResponse["Request-Body"] = requestBody
        val responseData = objectMapper.writeValueAsString(reportResponse)
        LOG.info("Response sent it to request zoho")
        compressionUtil.compressString(responseData!!)?.let { inQueueProducer.send(it) }
    }

    fun handleException(error: Throwable, requestBody: String){
        LOG.error("Error getting report", error)
        val retryObj = HashMap<String, Any>()
        retryObj["body"] = requestBody
        retryObj["error"] = error.message.toString()
        retryObj["retryCount"] = 0
        when (error) {
            is HttpClientResponseException -> {
                // Check for rate limiting
                if (error.status.code == 429) {
                    val retryAfter = error.response.header("Retry-After")
                    LOG.info("Rate limit hit. Retry after $retryAfter seconds.")
                    // Implement retry mechanism based on retryAfter
                    retryObj["retryAfter"] = retryAfter.toString()
                    sendReportToRetryQueue(retryObj)
                }
                // Handle other HTTP errors
                LOG.info("Error response: ${error.response.body()}")
                error.response.headers.forEach { LOG.info("Header: ${it.key} - Value: ${it.value}") }
            }

            is ReadTimeoutException -> {
                // Log and handle the read timeout
                LOG.error("Read timeout occurred: ${error.message}")
                // Implement retry logic or other error handling
                sendReportToRetryQueue(retryObj)
            }

            is IOException -> {
                LOG.error("IO Error: ${error.message}")
            }

            else -> {
                LOG.error("Unexpected Error: ${error.message}")
            }
        }
    }

    private fun sendReportToRetryQueue(retryObj: HashMap<String, Any>){
        val bodyWithRetry = objectMapper.writeValueAsString(retryObj)
        retryQueueProducer.send(bodyWithRetry)
    }

    companion object {
        val LOG = LoggerFactory.getLogger(BaseService::class.java)
    }
}