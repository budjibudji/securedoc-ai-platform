package com.securedoc.docmanagerservice.dao.repositories;

import com.securedoc.docmanagerservice.dao.entites.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
