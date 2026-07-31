package com.cloudvault.event.dto;

import com.cloudvault.event.model.VaultEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PublishEventRequest {

    @NotBlank(message = "File ID is required")
    private String fileId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Event type is required")
    private VaultEventType eventType;

    @NotBlank(message = "Filename is required")
    private String filename;

    private long fileSize;
    private boolean simulateFailure;

    public PublishEventRequest() {}

    public PublishEventRequest(String fileId, String userId, VaultEventType eventType, String filename, long fileSize, boolean simulateFailure) {
        this.fileId = fileId;
        this.userId = userId;
        this.eventType = eventType;
        this.filename = filename;
        this.fileSize = fileSize;
        this.simulateFailure = simulateFailure;
    }

    public static PublishEventRequestBuilder builder() {
        return new PublishEventRequestBuilder();
    }

    public static class PublishEventRequestBuilder {
        private String fileId;
        private String userId;
        private VaultEventType eventType;
        private String filename;
        private long fileSize;
        private boolean simulateFailure;

        public PublishEventRequestBuilder fileId(String fileId) { this.fileId = fileId; return this; }
        public PublishEventRequestBuilder userId(String userId) { this.userId = userId; return this; }
        public PublishEventRequestBuilder eventType(VaultEventType eventType) { this.eventType = eventType; return this; }
        public PublishEventRequestBuilder filename(String filename) { this.filename = filename; return this; }
        public PublishEventRequestBuilder fileSize(long fileSize) { this.fileSize = fileSize; return this; }
        public PublishEventRequestBuilder simulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; return this; }
        public PublishEventRequest build() { return new PublishEventRequest(fileId, userId, eventType, filename, fileSize, simulateFailure); }
    }

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
}
