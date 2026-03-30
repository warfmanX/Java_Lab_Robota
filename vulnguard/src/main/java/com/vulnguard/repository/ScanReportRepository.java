package com.vulnguard.repository;

import com.vulnguard.domain.ScanReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanReportRepository extends JpaRepository<ScanReport, Long> {
}

