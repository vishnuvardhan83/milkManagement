package com.dairyfarm.repository;

import com.dairyfarm.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByTenantId(String tenantId);
    List<SubscriptionPlan> findByTenantIdAndActiveTrue(String tenantId);
    List<SubscriptionPlan> findByTenantIdAndPlanType(String tenantId, SubscriptionPlan.PlanType planType);
}

