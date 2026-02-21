package com.uds.ged.domain.repository;

import com.uds.ged.domain.model.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    
    List<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(Long documentId);
    
    @Query("SELECT MAX(v.versionNumber) FROM DocumentVersion v WHERE v.document.id = :documentId")
    Optional<Integer> findMaxVersionNumberByDocumentId(@Param("documentId") Long documentId);
    
    @Query("SELECT v FROM DocumentVersion v WHERE v.document.id = :documentId ORDER BY v.versionNumber DESC LIMIT 1")
    Optional<DocumentVersion> findLatestVersionByDocumentId(@Param("documentId") Long documentId);
}
