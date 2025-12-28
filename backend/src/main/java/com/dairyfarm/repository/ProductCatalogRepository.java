package com.dairyfarm.repository;

import com.dairyfarm.entity.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {
    List<ProductCatalog> findByTenantId(String tenantId);
    List<ProductCatalog> findByTenantIdAndActiveTrue(String tenantId);
    List<ProductCatalog> findByTenantIdAndAvailableForSubscriptionTrue(String tenantId);
    List<ProductCatalog> findByTenantIdAndProductType(String tenantId, ProductCatalog.ProductType productType);
}

