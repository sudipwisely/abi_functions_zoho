package bi.accounting

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Factory
@Requires(property = "awslocal.services.dynamodb.endpoint-override")
class DynamoDbClientFactory {

    @Primary
    @Singleton
    fun dynamoDbClient(@Value("\${awslocal.services.dynamodb.endpoint-override}") endpointOverride: String): DynamoDbClient {
        val endpoint = URI.create(endpointOverride)
        return DynamoDbClient.builder()
            .endpointOverride(endpoint)
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
            .region(Region.of("us-east-1"))
            .build()
    }
}