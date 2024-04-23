package bi.accounting.repository

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@Singleton
class RequestRepository(
    private val dynamoDbClient: DynamoDbClient,
) {

    @Value("\${dynamodb.request-table-name}")
    private lateinit var tableName: String

    fun saveRequestData(requestId: String, userId: Long, orgIds: String, orgCount: Int, body: String): String {
        val item: MutableMap<String, AttributeValue?> = HashMap()
        item["pk"] = AttributeValue.builder().s("Request#$userId#$requestId").build()
        item["sk"] = AttributeValue.builder().n(Instant.now().toEpochMilli().toString()).build()
        item["orgIds"] = AttributeValue.builder().s(orgIds).build()
        item["orgCount"] = AttributeValue.builder().n(orgCount.toString()).build()
        item["body"] = AttributeValue.builder().s(body).build()
        try{
            val request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build()
            LOG.info("Saving request data {}", request)
            val response = dynamoDbClient.putItem(request)
            LOG.info("Saved request data {}", response.consumedCapacity())
            return response.toString()
        } catch (e: Exception) {
            LOG.error("Error saving request data", e)
            throw e
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(RequestRepository::class.java)
    }
}