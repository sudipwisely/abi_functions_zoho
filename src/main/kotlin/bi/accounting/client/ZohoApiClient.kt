package bi.accounting.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client(id = "zoho")
interface ZohoApiClient {

    @Get("https://books.zoho.com/api/v3/chartofaccounts")
    fun getReportChartofAccouts(
        @Header("Authorization") authorization: String,
        @QueryValue ("organization_id") orgId: String
    ): Mono<String?>
}