package bi.accounting.service

import bi.accounting.FunctionRequestHandler
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Factory
class ReportFactory {

    @Singleton
    fun reportServiceMap(reportServiceList: List<ReportService>): Map<String, ReportService> {
        LOG.info(reportServiceList.toString())
        return reportServiceList.associateBy { it::class.java.getAnnotation(Named::class.java).value }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FunctionRequestHandler::class.java)
    }
}