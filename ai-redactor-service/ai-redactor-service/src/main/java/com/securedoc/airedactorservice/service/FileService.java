// l'IA va récupérer la copie du PDF pour travailler dessus.

package com.securedoc.airedactorservice.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // Cette méthode télécharge le fichier depuis MinIO sous forme de flux (Stream)
    public InputStream downloadFile(String minioPath) {
        try {
            System.out.println("[AI] Téléchargement de : " + minioPath);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minioPath)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur téléchargement MinIO : " + e.getMessage());
        }
    }
}