package com.cloudvault.event.service;

import com.cloudvault.event.dto.EventResponse;
import com.cloudvault.event.dto.PublishEventRequest;
import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.model.VaultEventType;
import com.cloudvault.event.producer.VaultEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultEventServiceTest {

    @Mock
    private VaultEventProducer eventProducer;

    @InjectMocks
    private VaultEventService eventService;

    private PublishEventRequest publishRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventService, "fileEventsTopic", "file-vault-events");

        publishRequest = PublishEventRequest.builder()
                .fileId("file-101")
                .userId("user-55")
                .eventType(VaultEventType.FILE_UPLOADED)
                .filename("confidential_data.csv")
                .fileSize(1024L)
                .simulateFailure(false)
                .build();
    }

    @Test
    @DisplayName("Should publish event and return EventResponse")
    void testPublishEvent() {
        when(eventProducer.sendEvent(anyString(), any(FileVaultEvent.class))).thenReturn(CompletableFuture.completedFuture(null));

        EventResponse response = eventService.publishEvent(publishRequest);

        assertNotNull(response);
        assertEquals("ACCEPTED", response.getStatus());
        assertEquals("file-vault-events", response.getTopic());
        assertEquals("file-101", response.getEvent().getFileId());
        verify(eventProducer, times(1)).sendEvent(eq("file-vault-events"), any(FileVaultEvent.class));
    }

    @Test
    @DisplayName("Should record and retrieve processed and DLQ events correctly")
    void testRecordAndRetrieveEvents() {
        FileVaultEvent event1 = FileVaultEvent.builder().eventId("evt-1").fileId("file-1").build();
        FileVaultEvent eventDlq = FileVaultEvent.builder().eventId("evt-dlq-1").fileId("file-bad").build();

        eventService.recordProcessedEvent(event1);
        eventService.recordDlqEvent(eventDlq);

        List<FileVaultEvent> processed = eventService.getProcessedEvents();
        List<FileVaultEvent> dlq = eventService.getDlqEvents();

        assertEquals(1, processed.size());
        assertEquals("evt-1", processed.get(0).getEventId());

        assertEquals(1, dlq.size());
        assertEquals("evt-dlq-1", dlq.get(0).getEventId());
    }
}
