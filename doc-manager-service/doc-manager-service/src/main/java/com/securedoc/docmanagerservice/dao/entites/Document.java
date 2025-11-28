package com.securedoc.docmanagerservice.dao.entites;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String filename;

    private String minioPath;

    private LocalDateTime uploadTime;

    @Enumerated(EnumType.STRING)
    private DocStatus status;
}
