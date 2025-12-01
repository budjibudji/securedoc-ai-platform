package com.securedoc.airedactorservice.messaging;

import com.securedoc.airedactorservice.dto.DocumentMessage;
import com.securedoc.airedactorservice.service.FileService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class RabbitConsumer {

    private final FileService fileService;

    public RabbitConsumer(FileService fileService) {
        this.fileService = fileService;
    }

    // On reçoit directement l'objet DocumentMessage (converti auto depuis le JSON)
    @RabbitListener(queues = "scan-queue")
    public void receiveMessage(DocumentMessage message) {
        System.out.println("⚡ [AI-Redactor] Message reçu pour : " + message.getMinioPath());

        // Plus besoin de BDD ! On a déjà le chemin dans le message.

        try {
            // 1. Télécharger le fichier
            InputStream pdfStream = fileService.downloadFile(message.getMinioPath());
            System.out.println("[AI-Redactor] Fichier téléchargé avec succès !");

            // TODO: Étape suivante -> PDFBox et IA

            pdfStream.close();

        } catch (Exception e) {
            System.err.println("Erreur traitement : " + e.getMessage());
        }
    }
}