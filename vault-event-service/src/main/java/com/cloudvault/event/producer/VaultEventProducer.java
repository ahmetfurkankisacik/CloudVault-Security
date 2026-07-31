package com.cloudvault.event.producer;

import com.cloudvault.event.model.FileVaultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class VaultEventProducer {

    private static final Logger log = LoggerFactory.getLogger(VaultEventProducer.class);

    private final KafkaTemplate<String, FileVaultEvent> kafkaTemplate;

    public VaultEventProducer(KafkaTemplate<String, FileVaultEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, FileVaultEvent>> sendEvent(String topic, FileVaultEvent event) {
        log.info("Publishing FileVaultEvent to Kafka topic [{}]: EventID={}, FileID={}, Type={}",
                topic, event.getEventId(), event.getFileId(), event.getEventType());

        CompletableFuture<SendResult<String, FileVaultEvent>> future = kafkaTemplate.send(topic, event.getFileId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event successfully sent to topic [{}] partition [{}] at offset [{}]",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send Event [{}] to topic [{}]: {}", event.getEventId(), topic, ex.getMessage());
            }
        });

        return future;
    }
}
