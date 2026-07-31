package com.cloudvault.event.controller;

import com.cloudvault.event.dto.EventResponse;
import com.cloudvault.event.dto.PublishEventRequest;
import com.cloudvault.event.model.FileVaultEvent;
import com.cloudvault.event.service.VaultEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vault-events")
public class VaultEventController {

    private final VaultEventService eventService;

    public VaultEventController(VaultEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/publish")
    public ResponseEntity<EventResponse> publishEvent(@Valid @RequestBody PublishEventRequest request) {
        EventResponse response = eventService.publishEvent(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/processed")
    public ResponseEntity<List<FileVaultEvent>> getProcessedEvents() {
        return ResponseEntity.ok(eventService.getProcessedEvents());
    }

    @GetMapping("/dlq")
    public ResponseEntity<List<FileVaultEvent>> getDlqEvents() {
        return ResponseEntity.ok(eventService.getDlqEvents());
    }
}
