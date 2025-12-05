// exposer l'API REST.

package com.securedoc.docmanagerservice.controller;

import com.securedoc.docmanagerservice.dao.entities.Document;
import com.securedoc.docmanagerservice.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Autoriser le futur Frontend
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Document savedDoc = documentService.processUpload(file);
        return ResponseEntity.ok(savedDoc);
    }
}