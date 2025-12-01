package com.securedoc.docmanagerservice.service;

import com.securedoc.docmanagerservice.dao.entites.DocStatus;
import com.securedoc.docmanagerservice.dao.entites.Document;
import com.securedoc.docmanagerservice.dao.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final MinioService minioService;
    private final RabbitTemplate rabbitTemplate;

    public Document processUpload(MultipartFile file) {
        // 1. Créer l'entité en BDD (Méthode sans Builder)
        String generatedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // On utilise le constructeur classique au lieu de .builder()
        Document doc = new Document(
                file.getOriginalFilename(),
                generatedFileName,
                LocalDateTime.now(),
                DocStatus.PENDING
        );

        doc = repository.save(doc);

        // 2. Uploader vers MinIO
        minioService.uploadFile(generatedFileName, file);

        // 3. Envoyer message RabbitMQ
        rabbitTemplate.convertAndSend("scan-queue", doc.getId());

        System.out.println("[DocManager] Fichier traité avec succès. ID: " + doc.getId());

        return doc;
    }
}