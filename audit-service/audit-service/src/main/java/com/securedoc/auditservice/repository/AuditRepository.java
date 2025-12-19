package com.securedoc.auditservice.repository;

import com.securedoc.auditservice.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<AuditLog, String> {
}