// exposer l'API REST.

package com.securedoc.docmanagerservice.controller;

import com.securedoc.docmanagerservice.dao.entities.Document;
import com.securedoc.docmanagerservice.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file,
                                           JwtAuthenticationToken auth) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String userId = auth.getToken().getClaim("sub"); // Keycloak user UUID

        Document savedDoc = documentService.processUpload(file,userId);
        return ResponseEntity.ok(savedDoc);
    }

    // NEW : récupérer tous les documents

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(JwtAuthenticationToken auth) {

        String userId = auth.getToken().getClaim("sub"); // Keycloak user UUID

        List<Document> docs = documentService.getAllDocuments(userId);

        return ResponseEntity.ok(docs);
    }



}
