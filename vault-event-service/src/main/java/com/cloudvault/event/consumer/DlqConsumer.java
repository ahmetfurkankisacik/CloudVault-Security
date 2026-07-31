package com.cloudvault.event.consumer;

import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.service.VaultEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqConsumer.class);

    private final VaultEventService eventService;

    public DlqConsumer(VaultEventService eventService) {
        this.eventService = eventService;
    }

    @KafkaListener(topics = "${cloudvault.kafka.topics.dlq-events:file-vault-events.DLQ}", groupId = "cloudvault-dlq-group")
    public void consumeDlq(FileVaultEvent event) {
        log.warn("🚨 ALERT: Received event in Dead Letter Queue (DLQ)! EventID={}, FileID={}, Filename={}",
                event.getEventId(), event.getFileId(), event.getFilename());

        eventService.recordDlqEvent(event);
        log.info("Event [{}] successfully quarantined in DLQ store.", event.getEventId());
    }
}
