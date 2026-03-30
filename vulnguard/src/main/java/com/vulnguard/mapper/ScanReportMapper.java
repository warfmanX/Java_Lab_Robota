package com.vulnguard.mapper;

import com.vulnguard.domain.ScanReport;
import com.vulnguard.domain.SystemAsset;
import com.vulnguard.domain.Vulnerability;
import com.vulnguard.dto.ScanReportDto;
import com.vulnguard.web.api.error.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScanReportMapper {

    private ScanReportMapper() {}

    public static ScanReportDto toDto(ScanReport entity) {
        if (entity == null) return null;
        ScanReportDto dto = new ScanReportDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setTimestamp(entity.getTimestamp());
        dto.setAssetId(entity.getAsset().getId());
        dto.setVulnerabilityIds(
                entity.getVulnerabilities().stream()
                        .map(Vulnerability::getId)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    /**
     * Create/Update conversion requires repositories to resolve foreign keys.
     * To keep the mapper pure we delegate existence checks to the caller.
     */
    public static ScanReport toEntity(ScanReportDto dto,
                                      SystemAsset asset,
                                      List<Vulnerability> vulnerabilities) {
        if (dto == null) return null;
        ScanReport entity = new ScanReport();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setTimestamp(dto.getTimestamp());
        entity.setAsset(asset);
        entity.setVulnerabilities(new HashSet<>(vulnerabilities));
        return entity;
    }
}
