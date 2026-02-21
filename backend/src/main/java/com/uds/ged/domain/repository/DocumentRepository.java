package com.uds.ged.domain.repository;

import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE " +
           "(:title IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR d.status = :status)")
    Page<Document> findByFilters(
        @Param("title") String title,
        @Param("status") DocumentStatus status,
        Pageable pageable
    );
    
    Page<Document> findByOwnerId(Long ownerId, Pageable pageable);
    
    Page<Document> findByTenantId(String tenantId, Pageable pageable);
}
