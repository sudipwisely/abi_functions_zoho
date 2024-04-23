package bi.accounting.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client(id ="accounts")
interface AccountServiceClient {

    @Get("/accounts/token")
    fun getAccountToken(@Header("X-UserID") userId: String, @Header("X-OrgID") orgId: String): Mono<String>

    @Get("/accounts-service/accounts/{orgId}")
    fun getAccount(@Header("X-UserID") userId: String, orgId: String): Mono<String>
}