package com.securedoc.docmanagerservice.dao.repositories;

import com.securedoc.docmanagerservice.dao.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, String> {
}
