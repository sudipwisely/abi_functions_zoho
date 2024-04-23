package bi.accounting

import io.micronaut.jms.annotations.JMSProducer
import io.micronaut.jms.annotations.Queue
import io.micronaut.jms.sqs.configuration.SqsConfiguration
import io.micronaut.messaging.annotation.MessageBody

@JMSProducer(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
interface RetryQueueProducer {

    @Queue("retry-queue")
    fun send(@MessageBody message: String)
}