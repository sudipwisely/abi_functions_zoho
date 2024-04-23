package bi.accounting

import io.micronaut.jms.annotations.JMSProducer
import io.micronaut.jms.annotations.Queue
import io.micronaut.jms.sqs.configuration.SqsConfiguration
import io.micronaut.messaging.annotation.MessageBody

@JMSProducer(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
interface InQueueProducer {

    @Queue("report-in-queue-zoho")
    fun send(@MessageBody message: String)
}