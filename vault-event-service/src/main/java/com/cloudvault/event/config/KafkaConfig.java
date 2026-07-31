package com.cloudvault.event.config;

import com.cloudvault.event.model.FileVaultEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${cloudvault.kafka.topics.file-events:file-vault-events}")
    private String fileEventsTopic;

    @Value("${cloudvault.kafka.topics.dlq-events:file-vault-events.DLQ}")
    private String dlqEventsTopic;

    @Value("${cloudvault.kafka.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${cloudvault.kafka.retry.backoff-ms:1000}")
    private long backoffMs;

    @Bean
    public NewTopic fileVaultEventsTopic() {
        return TopicBuilder.name(fileEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fileVaultEventsDlqTopic() {
        return TopicBuilder.name(dlqEventsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, FileVaultEvent> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> {
                    log.error("Retries exhausted for record in topic [{}] partition [{}] offset [{}]. Routing to DLQ [{}]",
                            record.topic(), record.partition(), record.offset(), dlqEventsTopic);
                    return new TopicPartition(dlqEventsTopic, 0);
                });

        FixedBackOff backOff = new FixedBackOff(backoffMs, maxAttempts - 1);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Retry attempt #{} failed for Event ID [{}] in topic [{}]: {}",
                        deliveryAttempt, record.key(), record.topic(), ex.getMessage())
        );

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FileVaultEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, FileVaultEvent> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, FileVaultEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
