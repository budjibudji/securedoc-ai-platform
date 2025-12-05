package com.securedoc.docmanagerservice.messaging;

import com.securedoc.docmanagerservice.dao.entities.DocStatus;
import com.securedoc.docmanagerservice.dao.entities.Document;
import com.securedoc.docmanagerservice.dao.repositories.DocumentRepository;
import com.securedoc.docmanagerservice.dto.DocumentProcessedMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ProcessedConsumer {

    private final DocumentRepository repository;

    public ProcessedConsumer(DocumentRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "processed-queue")
    public void consumeProcessed(DocumentProcessedMessage message) {

        System.out.println("📥 [DocManager] Message reçu : Document PROCESSED pour ID = " + message.getDocumentId());

        // 1 — Récupérer le document en base
        Document doc = repository.findById(message.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document introuvable : " + message.getDocumentId()));

        // 2 — Mettre à jour le statut
        doc.setStatus(DocStatus.PROCESSED);

        // 3 — Enregistrer le chemin du PDF clean
        doc.setCleanMinioPath(message.getCleanMinioPath());

        // 4 — Sauvegarder en base
        repository.save(doc);

        System.out.println("✔ [DocManager] Document mis à jour avec succès : PROCESSED → " + message.getCleanMinioPath());
    }
}
