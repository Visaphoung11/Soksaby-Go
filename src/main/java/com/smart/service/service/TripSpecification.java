package com.smart.service.service;

import com.smart.service.entity.TripEntity;
import com.smart.service.enums.TripStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TripSpecification {
    public static Specification<TripEntity> hasFilters(String origin, String destination, LocalDate date) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Mandatory: Only show AVAILABLE and NOT DELETED trips
            predicates.add(cb.equal(root.get("status"), TripStatus.AVAILABLE));
            predicates.add(cb.equal(root.get("deleted"), false));

            // 2. Optional: Origin (Case Insensitive)
            if (origin != null && !origin.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("origin")), "%" + origin.toLowerCase() + "%"));
            }

            // 3. Optional: Destination
            if (destination != null && !destination.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("destination")), "%" + destination.toLowerCase() + "%"));
            }

            // 4. Optional: Date (Start to End of Day)
            if (date != null) {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("departureTime"), start, end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}