package com.uds.ged.domain.repository;

import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    
    Page<Document> findByOwnerId(Long ownerId, Pageable pageable);
    
    Page<Document> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * Find document by ID with tags eagerly loaded.
     * Required when spring.jpa.open-in-view=false to avoid LazyInitializationException.
     */
    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.tags WHERE d.id = :id")
    Optional<Document> findByIdWithTags(@Param("id") Long id);
    
    /**
     * Find all documents with tags and owner eagerly loaded.
     * Required when spring.jpa.open-in-view=false to avoid LazyInitializationException.
     */
    @Query(value = "SELECT DISTINCT d FROM Document d LEFT JOIN FETCH d.tags LEFT JOIN FETCH d.owner",
           countQuery = "SELECT COUNT(DISTINCT d) FROM Document d")
    Page<Document> findAllWithTagsAndOwner(Pageable pageable);
}
