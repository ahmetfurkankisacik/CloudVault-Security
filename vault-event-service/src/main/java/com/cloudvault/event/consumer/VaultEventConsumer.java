package com.cloudvault.event.consumer;

import com.cloudvault.event.exception.EventProcessingException;
import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.service.VaultEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class VaultEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(VaultEventConsumer.class);

    private final VaultEventService eventService;

    public VaultEventConsumer(VaultEventService eventService) {
        this.eventService = eventService;
    }

    @KafkaListener(topics = "${cloudvault.kafka.topics.file-events:file-vault-events}", groupId = "${spring.kafka.consumer.group-id:cloudvault-event-group}")
    public void consume(FileVaultEvent event) {
        log.info("Received FileVaultEvent from Kafka: EventID={}, FileID={}, Type={}, SimulateFailure={}",
                event.getEventId(), event.getFileId(), event.getEventType(), event.isSimulateFailure());

        if (event.isSimulateFailure()) {
            log.error("Simulated Failure triggered for Event ID [{}]! Throwing EventProcessingException...", event.getEventId());
            throw new EventProcessingException("Simulated processing exception for event: " + event.getEventId());
        }

        eventService.recordProcessedEvent(event);
        log.info("FileVaultEvent [{}] successfully processed and logged.", event.getEventId());
    }
}
