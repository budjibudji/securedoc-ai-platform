package com.securedoc.docmanagerservice.service;

import com.securedoc.docmanagerservice.dao.entities.DocStatus;
import com.securedoc.docmanagerservice.dao.entities.Document;
import com.securedoc.docmanagerservice.dao.repositories.DocumentRepository;
import com.securedoc.docmanagerservice.dto.DocumentMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final MinioService minioService;
    private final RabbitTemplate rabbitTemplate;

    public Document processUpload(MultipartFile file, String userId) {
        // 1. Créer l'entité en BDD (Méthode sans Builder)
        String generatedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // On utilise le constructeur classique au lieu de .builder()
        Document doc = new Document(
                file.getOriginalFilename(),
                generatedFileName,
                LocalDateTime.now(),
                DocStatus.PENDING,
                userId
        );

        doc = repository.save(doc);

        // 2. Uploader vers MinIO
        minioService.uploadFile(generatedFileName, file);

        // 3. Envoyer message RabbitMQ (id + path)
        DocumentMessage message = new DocumentMessage(doc.getId(), doc.getMinioPath());

        rabbitTemplate.convertAndSend("doc-exchange", "", message);

        System.out.println("✅ [DocManager] Message diffusé à l'IA et à l'Audit via doc-exchange");

        System.out.println("[DocManager] Fichier traité avec succès. ID: " + doc.getId());

        return doc;
    }

    //    public List<Document> getAllDocuments() {
//        //return repository.findAll(Sort.by(Sort.Direction.DESC, "uploadTime"));
//
//
//    }
    public List<Document> getAllDocuments(String userId) {
        return repository.findByCreatedByOrderByUploadTimeDesc(userId);
    }
    // ----------------------------
    // NEW : Récupérer un document par ID
    // ----------------------------
}