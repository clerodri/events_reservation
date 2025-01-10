package com.clerodri.details.repository.jpa;


import com.clerodri.details.entity.EventEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface EventJpaRepository  extends JpaRepository<EventEntity, Long>,
        JpaSpecificationExecutor<EventEntity> {

    List<EventEntity> findAll(Sort sort);
}
