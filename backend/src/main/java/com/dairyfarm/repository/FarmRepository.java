package com.dairyfarm.repository;

import com.dairyfarm.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByTenantId(String tenantId);
    boolean existsByTenantId(String tenantId);
}

