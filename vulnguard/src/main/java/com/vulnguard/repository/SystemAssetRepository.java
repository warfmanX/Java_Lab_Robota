package com.vulnguard.repository;

import com.vulnguard.domain.SystemAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemAssetRepository extends JpaRepository<SystemAsset, Long> {
}

