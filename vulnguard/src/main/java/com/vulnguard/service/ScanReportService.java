package com.vulnguard.service;

import com.vulnguard.domain.ScanReport;
import com.vulnguard.domain.SystemAsset;
import com.vulnguard.domain.Vulnerability;
import com.vulnguard.dto.ScanReportDto;
import com.vulnguard.repository.ScanReportRepository;
import com.vulnguard.repository.SystemAssetRepository;
import com.vulnguard.repository.VulnerabilityRepository;
import com.vulnguard.mapper.ScanReportMapper;
import com.vulnguard.web.api.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScanReportService {

    private final ScanReportRepository scanReportRepository;
    private final SystemAssetRepository systemAssetRepository;
    private final VulnerabilityRepository vulnerabilityRepository;

    public ScanReportService(ScanReportRepository scanReportRepository,
                             SystemAssetRepository systemAssetRepository,
                             VulnerabilityRepository vulnerabilityRepository) {
        this.scanReportRepository = scanReportRepository;
        this.systemAssetRepository = systemAssetRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    public List<ScanReportDto> findAll() {
        return scanReportRepository.findAll()
                .stream()
                .map(ScanReportMapper::toDto)
                .collect(Collectors.toList());
    }

    public ScanReportDto findById(Long id) {
        ScanReport report = scanReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ScanReport not found: " + id));
        return ScanReportMapper.toDto(report);
    }

    public ScanReportDto create(ScanReportDto dto) {
        // verify asset exists
        SystemAsset asset = systemAssetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new NotFoundException("SystemAsset not found: " + dto.getAssetId()));

        // load vulnerabilities and ensure all requested IDs are present
        List<Vulnerability> vulns = vulnerabilityRepository.findAllById(dto.getVulnerabilityIds());
        if (vulns.size() != dto.getVulnerabilityIds().size()) {
            throw new NotFoundException("One or more vulnerabilities not found");
        }

        ScanReport report = ScanReportMapper.toEntity(dto, asset, vulns);
        report.setId(null);
        return ScanReportMapper.toDto(scanReportRepository.save(report));
    }

    public ScanReportDto update(Long id, ScanReportDto dto) {
        ScanReport existing = scanReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ScanReport not found: " + id));

        SystemAsset asset = systemAssetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new NotFoundException("SystemAsset not found: " + dto.getAssetId()));

        List<Vulnerability> vulns = vulnerabilityRepository.findAllById(dto.getVulnerabilityIds());
        if (vulns.size() != dto.getVulnerabilityIds().size()) {
            throw new NotFoundException("One or more vulnerabilities not found");
        }

        existing = ScanReportMapper.toEntity(dto, asset, vulns);
        existing.setId(id);
        return ScanReportMapper.toDto(scanReportRepository.save(existing));
    }

    public void delete(Long id) {
        if (!scanReportRepository.existsById(id)) {
            throw new NotFoundException("ScanReport not found: " + id);
        }
        scanReportRepository.deleteById(id);
    }

}

