package com.securedoc.docmanagerservice.dao.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String filename;

    private String minioPath;

    // 👉 Nouveau champ (PDF flouté)
    private String cleanMinioPath;

    private LocalDateTime uploadTime;

    @Enumerated(EnumType.STRING)
    private DocStatus status;

    // --- Constructeurs ---
    public Document() {
    }

    public Document(String filename, String minioPath, LocalDateTime uploadTime, DocStatus status) {
        this.filename = filename;
        this.minioPath = minioPath;
        this.uploadTime = uploadTime;
        this.status = status;
    }

    // --- Getters et Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMinioPath() {
        return minioPath;
    }

    public void setMinioPath(String minioPath) {
        this.minioPath = minioPath;
    }

    public String getCleanMinioPath() {
        return cleanMinioPath;
    }

    public void setCleanMinioPath(String cleanMinioPath) {
        this.cleanMinioPath = cleanMinioPath;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public DocStatus getStatus() {
        return status;
    }

    public void setStatus(DocStatus status) {
        this.status = status;
    }
}
