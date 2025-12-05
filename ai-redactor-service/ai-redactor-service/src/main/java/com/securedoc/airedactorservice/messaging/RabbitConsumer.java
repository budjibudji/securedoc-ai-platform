package com.securedoc.airedactorservice.messaging;

import com.securedoc.airedactorservice.dto.DocumentMessage;
import com.securedoc.airedactorservice.dto.DocumentProcessedMessage;
import com.securedoc.airedactorservice.service.AiService;
import com.securedoc.airedactorservice.service.FileService;
import com.securedoc.airedactorservice.service.RedactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class RabbitConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final FileService fileService;
    private final AiService aiService;
    private final RedactionService redactionService;

    public RabbitConsumer(FileService fileService,
                          AiService aiService,
                          RedactionService redactionService,
                          RabbitTemplate rabbitTemplate) {

        this.fileService = fileService;
        this.aiService = aiService;
        this.redactionService = redactionService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "scan-queue")
    public void receiveMessage(DocumentMessage message) {
        System.out.println("⚡ [AI] Début du traitement pour : " + message.getMinioPath());

        try {
            // 1 — Télécharger le fichier (extraction texte)
            InputStream streamForText = fileService.downloadFile(message.getMinioPath());
            System.out.println("[AI] Téléchargement OK (texte)");

            // 2 — Extraction du texte
            String fullText = aiService.extractText(streamForText);
            System.out.println("[AI] Texte extrait (longueur = " + fullText.length() + ")");

            // 3 — Détection des informations sensibles
            List<String> sensitiveWords = aiService.detectSensitiveInfo(fullText);
            System.out.println("🔍 Mots sensibles trouvés : " + sensitiveWords);

            // 4 — Télécharger à nouveau pour le floutage
            InputStream streamForRedaction = fileService.downloadFile(message.getMinioPath());

            // 5 — Générer le PDF flouté
            byte[] redactedPdfBytes = redactionService.redactDocument(streamForRedaction, sensitiveWords);
            System.out.println("✅ PDF flouté généré ! Taille : " + redactedPdfBytes.length + " bytes");

            // 🌟 ÉTAPE 8 — Upload du PDF Clean dans MinIO
            String cleanFileName = fileService.uploadCleanPdf(message.getMinioPath(), redactedPdfBytes);
            System.out.println("📤 PDF Clean uploadé dans MinIO : " + cleanFileName);

            // 🌟 Envoi du message "processed" au DocManager
            DocumentProcessedMessage processedMsg =
                    new DocumentProcessedMessage(message.getId(), cleanFileName);

            rabbitTemplate.convertAndSend("processed-queue", processedMsg);
            System.out.println("📨 Message envoyé au DocManager : Document PROCESSED");

        } catch (Exception e) {
            System.err.println("❌ Erreur traitement IA : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
