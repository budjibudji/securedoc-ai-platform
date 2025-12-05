package com.securedoc.docmanagerservice.dto;

import java.io.Serializable;

// C'est une classe simple (POJO) pour transporter les données
public class DocumentMessage {
    private String id;
    private String minioPath;

    public DocumentMessage() {}

    public DocumentMessage(String id, String minioPath) {
        this.id = id;
        this.minioPath = minioPath;
    }

    // Getters et Setters obligatoires pour le JSON
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMinioPath() { return minioPath; }
    public void setMinioPath(String minioPath) { this.minioPath = minioPath; }
}