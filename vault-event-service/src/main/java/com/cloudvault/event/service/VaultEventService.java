package com.cloudvault.event.service;

import com.cloudvault.event.dto.EventResponse;
import com.cloudvault.event.dto.PublishEventRequest;
import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.producer.VaultEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class VaultEventService {

    private final VaultEventProducer eventProducer;
    private final List<FileVaultEvent> processedEvents = Collections.synchronizedList(new ArrayList<>());
    private final List<FileVaultEvent> dlqEvents = Collections.synchronizedList(new ArrayList<>());

    @Value("${cloudvault.kafka.topics.file-events:file-vault-events}")
    private String fileEventsTopic;

    public VaultEventService(VaultEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public EventResponse publishEvent(PublishEventRequest request) {
        String eventId = UUID.randomUUID().toString();

        FileVaultEvent event = FileVaultEvent.builder()
                .eventId(eventId)
                .fileId(request.getFileId())
                .userId(request.getUserId())
                .eventType(request.getEventType())
                .filename(request.getFilename())
                .fileSize(request.getFileSize())
                .simulateFailure(request.isSimulateFailure())
                .timestamp(LocalDateTime.now())
                .build();

        eventProducer.sendEvent(fileEventsTopic, event);

        return EventResponse.builder()
                .status("ACCEPTED")
                .message("Event successfully published to Kafka topic")
                .topic(fileEventsTopic)
                .event(event)
                .build();
    }

    public void recordProcessedEvent(FileVaultEvent event) {
        processedEvents.add(event);
    }

    public void recordDlqEvent(FileVaultEvent event) {
        dlqEvents.add(event);
    }

    public List<FileVaultEvent> getProcessedEvents() {
        return new ArrayList<>(processedEvents);
    }

    public List<FileVaultEvent> getDlqEvents() {
        return new ArrayList<>(dlqEvents);
    }

    public void clearState() {
        processedEvents.clear();
        dlqEvents.clear();
    }
}
