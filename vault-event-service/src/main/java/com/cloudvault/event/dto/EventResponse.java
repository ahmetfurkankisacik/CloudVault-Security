package com.cloudvault.event.dto;

import com.cloudvault.event.model.FileVaultEvent;

public class EventResponse {

    private String status;
    private String message;
    private String topic;
    private FileVaultEvent event;

    public EventResponse() {}

    public EventResponse(String status, String message, String topic, FileVaultEvent event) {
        this.status = status;
        this.message = message;
        this.topic = topic;
        this.event = event;
    }

    public static EventResponseBuilder builder() {
        return new EventResponseBuilder();
    }

    public static class EventResponseBuilder {
        private String status;
        private String message;
        private String topic;
        private FileVaultEvent event;

        public EventResponseBuilder status(String status) { this.status = status; return this; }
        public EventResponseBuilder message(String message) { this.message = message; return this; }
        public EventResponseBuilder topic(String topic) { this.topic = topic; return this; }
        public EventResponseBuilder event(FileVaultEvent event) { this.event = event; return this; }
        public EventResponse build() { return new EventResponse(status, message, topic, event); }
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public FileVaultEvent getEvent() { return event; }
    public void setEvent(FileVaultEvent event) { this.event = event; }
}
