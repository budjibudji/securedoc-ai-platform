package com.securedoc.auditservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "audit_logs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class AuditLog {
    @Id
    private String id;
    private String eventType; // "UPLOAD", "PROCESSED", etc.
    private String documentId;
    private String filename;
    private String message;
    private LocalDateTime timestamp;
}
