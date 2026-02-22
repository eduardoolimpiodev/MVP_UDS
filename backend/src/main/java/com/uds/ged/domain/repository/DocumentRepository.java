package com.uds.ged.domain.repository;

import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    
    Page<Document> findByOwnerId(Long ownerId, Pageable pageable);
    
    Page<Document> findByTenantId(String tenantId, Pageable pageable);
}
