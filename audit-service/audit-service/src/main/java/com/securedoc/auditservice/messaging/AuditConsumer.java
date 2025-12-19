package com.securedoc.auditservice.messaging;

import com.securedoc.auditservice.dto.DocumentMessage;
import com.securedoc.auditservice.entity.AuditLog;
import com.securedoc.auditservice.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditConsumer {

    private final AuditRepository repository;

    @RabbitListener(queues = "audit-queue")
    public void logEvent(DocumentMessage message) {

        LocalDateTime now = LocalDateTime.now();

        // 1. Sauvegarde en Base (Pour la recherche/Admin)
        AuditLog logEntity = AuditLog.builder()
                .eventType("FILE_RECEIVED")
                .documentId(message.getId())
                .filename(message.getMinioPath())
                .message("Document reçu pour traitement")
                .timestamp(now)
                .build();
        repository.save(logEntity);

        // 2. Écriture dans le fichier LOG (Format lisible pour humain)
        // Format: [DATE HEURE] | ACTION | FICHIER
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String logLine = String.format("[%s] | RECU | Fichier: %s | ID: %s",
                formattedDate,
                message.getMinioPath(),
                message.getId());

        // Ceci ira dans le fichier audit-service.log SANS le bruit technique
        log.info(logLine);
    }
}