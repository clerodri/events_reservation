package com.clerodri.details.filter;

import com.clerodri.details.entity.EventEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventSpecifications {

    public static Specification<EventEntity> hasName(String name) {
        return (Root<EventEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            if (name == null || name.isBlank()) {
                return null;
            }
            return builder.like(
                    builder.lower(root.get("eventName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<EventEntity> hasDate(String date) {
        return (Root<EventEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            if (date == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
            return builder.between(root.get("eventDateTime"), startOfDay, endOfDay);
        };
    }

    public static Specification<EventEntity> hasLocation(String location) {
        return (Root<EventEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            if (location == null || location.isBlank()) {
                return null;
            }
            return builder.like(
                    builder.lower(root.get("eventName")), "%" + location.toLowerCase() + "%");
        };
    }
}
