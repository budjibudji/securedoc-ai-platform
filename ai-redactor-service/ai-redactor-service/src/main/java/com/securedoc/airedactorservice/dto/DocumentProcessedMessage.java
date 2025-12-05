package com.securedoc.airedactorservice.dto;

import java.io.Serializable;

public class DocumentProcessedMessage implements Serializable {

    private String documentId;
    private String cleanMinioPath;

    public DocumentProcessedMessage() {}

    public DocumentProcessedMessage(String documentId, String cleanMinioPath) {
        this.documentId = documentId;
        this.cleanMinioPath = cleanMinioPath;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String id) { this.documentId = id; }

    public String getCleanMinioPath() { return cleanMinioPath; }
    public void setCleanMinioPath(String cleanPath) { this.cleanMinioPath = cleanPath; }
}
