package com.cloudvault.event.controller;

import com.cloudvault.event.dto.EventResponse;
import com.cloudvault.event.dto.PublishEventRequest;
import com.cloudvault.event.exception.GlobalExceptionHandler;
import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.model.VaultEventType;
import com.cloudvault.event.service.VaultEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VaultEventController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class VaultEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VaultEventService eventService;

    private FileVaultEvent mockEvent;
    private EventResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockEvent = FileVaultEvent.builder()
                .eventId("evt-test-1")
                .fileId("file-888")
                .userId("user-777")
                .eventType(VaultEventType.FILE_UPLOADED)
                .filename("test-document.pdf")
                .fileSize(5000L)
                .simulateFailure(false)
                .build();

        mockResponse = EventResponse.builder()
                .status("ACCEPTED")
                .message("Event successfully published to Kafka topic")
                .topic("file-vault-events")
                .event(mockEvent)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/vault-events/publish - Should return 202 Accepted and EventResponse")
    void testPublishEventSuccess() throws Exception {
        PublishEventRequest request = PublishEventRequest.builder()
                .fileId("file-888")
                .userId("user-777")
                .eventType(VaultEventType.FILE_UPLOADED)
                .filename("test-document.pdf")
                .fileSize(5000L)
                .simulateFailure(false)
                .build();

        when(eventService.publishEvent(any(PublishEventRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/vault-events/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.topic").value("file-vault-events"))
                .andExpect(jsonPath("$.event.eventId").value("evt-test-1"));
    }

    @Test
    @DisplayName("GET /api/v1/vault-events/processed - Should return 200 OK and list of processed events")
    void testGetProcessedEvents() throws Exception {
        when(eventService.getProcessedEvents()).thenReturn(List.of(mockEvent));

        mockMvc.perform(get("/api/v1/vault-events/processed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("evt-test-1"))
                .andExpect(jsonPath("$[0].filename").value("test-document.pdf"));
    }

    @Test
    @DisplayName("GET /api/v1/vault-events/dlq - Should return 200 OK and list of DLQ events")
    void testGetDlqEvents() throws Exception {
        FileVaultEvent dlqEvent = FileVaultEvent.builder()
                .eventId("evt-dlq-99")
                .fileId("file-corrupted")
                .filename("corrupted.bin")
                .build();

        when(eventService.getDlqEvents()).thenReturn(List.of(dlqEvent));

        mockMvc.perform(get("/api/v1/vault-events/dlq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("evt-dlq-99"))
                .andExpect(jsonPath("$[0].filename").value("corrupted.bin"));
    }
}
