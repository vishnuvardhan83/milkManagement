package com.dairyfarm.repository;

import com.dairyfarm.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    Optional<SystemSettings> findByTenantIdAndSettingKey(String tenantId, String settingKey);
    List<SystemSettings> findByTenantId(String tenantId);
}

