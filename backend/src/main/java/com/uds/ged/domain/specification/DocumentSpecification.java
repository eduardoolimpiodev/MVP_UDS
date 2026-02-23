package com.uds.ged.domain.specification;

import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.enums.DocumentStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> withFilters(String title, DocumentStatus status) {
        return (root, query, criteriaBuilder) -> {
            // Add fetch joins to eagerly load collections (required when open-in-view=false)
            if (query.getResultType().equals(Document.class)) {
                root.fetch("tags", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("owner", jakarta.persistence.criteria.JoinType.LEFT);
                query.distinct(true);
            }
            
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(root.get("title")),
                    "%" + title.toUpperCase() + "%"
                ));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
