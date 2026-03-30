package com.vulnguard.dto;

import com.vulnguard.domain.ScanReport;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class ScanReportDto {

    private Long id;

    @NotNull
    private ScanReport.Status status;

    @NotNull
    private Instant timestamp;

    @NotNull
    private Long assetId;

    @NotEmpty
    private List<Long> vulnerabilityIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScanReport.Status getStatus() {
        return status;
    }

    public void setStatus(ScanReport.Status status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public List<Long> getVulnerabilityIds() {
        return vulnerabilityIds;
    }

    public void setVulnerabilityIds(List<Long> vulnerabilityIds) {
        this.vulnerabilityIds = vulnerabilityIds;
    }
}

