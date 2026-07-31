package com.cloudvault.event.producer;

import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.model.VaultEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultEventProducerTest {

    @Mock
    private KafkaTemplate<String, FileVaultEvent> kafkaTemplate;

    @InjectMocks
    private VaultEventProducer vaultEventProducer;

    private FileVaultEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = FileVaultEvent.builder()
                .eventId("evt-12345")
                .fileId("file-99")
                .userId("user-42")
                .eventType(VaultEventType.FILE_UPLOADED)
                .filename("security-report.pdf")
                .fileSize(2048500L)
                .simulateFailure(false)
                .build();
    }

    @Test
    @DisplayName("Should publish FileVaultEvent to Kafka topic successfully")
    void testSendEventSuccess() {
        CompletableFuture<SendResult<String, FileVaultEvent>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq("file-vault-events"), eq("file-99"), any(FileVaultEvent.class))).thenReturn(future);

        CompletableFuture<SendResult<String, FileVaultEvent>> result = vaultEventProducer.sendEvent("file-vault-events", testEvent);

        assertNotNull(result);
        verify(kafkaTemplate, times(1)).send(eq("file-vault-events"), eq("file-99"), any(FileVaultEvent.class));
    }
}
