package com.cloudvault.event.model;

import java.time.LocalDateTime;

public class FileVaultEvent {

    private String eventId;
    private String fileId;
    private String userId;
    private VaultEventType eventType;
    private String filename;
    private long fileSize;
    private boolean simulateFailure;
    private LocalDateTime timestamp;

    public FileVaultEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public FileVaultEvent(String eventId, String fileId, String userId, VaultEventType eventType, String filename, long fileSize, boolean simulateFailure, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.fileId = fileId;
        this.userId = userId;
        this.eventType = eventType;
        this.filename = filename;
        this.fileSize = fileSize;
        this.simulateFailure = simulateFailure;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public static FileVaultEventBuilder builder() {
        return new FileVaultEventBuilder();
    }

    public static class FileVaultEventBuilder {
        private String eventId;
        private String fileId;
        private String userId;
        private VaultEventType eventType;
        private String filename;
        private long fileSize;
        private boolean simulateFailure;
        private LocalDateTime timestamp = LocalDateTime.now();

        public FileVaultEventBuilder eventId(String eventId) { this.eventId = eventId; return this; }
        public FileVaultEventBuilder fileId(String fileId) { this.fileId = fileId; return this; }
        public FileVaultEventBuilder userId(String userId) { this.userId = userId; return this; }
        public FileVaultEventBuilder eventType(VaultEventType eventType) { this.eventType = eventType; return this; }
        public FileVaultEventBuilder filename(String filename) { this.filename = filename; return this; }
        public FileVaultEventBuilder fileSize(long fileSize) { this.fileSize = fileSize; return this; }
        public FileVaultEventBuilder simulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; return this; }
        public FileVaultEventBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public FileVaultEvent build() { return new FileVaultEvent(eventId, fileId, userId, eventType, filename, fileSize, simulateFailure, timestamp); }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public VaultEventType getEventType() { return eventType; }
    public void setEventType(VaultEventType eventType) { this.eventType = eventType; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public boolean isSimulateFailure() { return simulateFailure; }
    public void setSimulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
