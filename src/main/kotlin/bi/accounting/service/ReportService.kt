package bi.accounting.service

interface ReportService {
    fun generateReport(reportRequest: ReportRequest)
}