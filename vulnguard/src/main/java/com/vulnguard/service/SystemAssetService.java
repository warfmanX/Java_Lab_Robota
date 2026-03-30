package com.vulnguard.service;

import com.vulnguard.domain.ScanReport;
import com.vulnguard.domain.SystemAsset;
import com.vulnguard.domain.Vulnerability;
import com.vulnguard.dto.SystemAssetDto;
import com.vulnguard.repository.ScanReportRepository;
import com.vulnguard.repository.SystemAssetRepository;
import com.vulnguard.repository.VulnerabilityRepository;
import com.vulnguard.web.api.error.NotFoundException;
import com.vulnguard.mapper.SystemAssetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of RandomProvider using java.util.Random
 */
class DefaultRandomProvider implements RandomProvider {
    private final Random random = new Random();

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }
}

@Service
@Transactional
public class SystemAssetService {

    private final SystemAssetRepository assetRepository;
    private final ScanReportRepository scanReportRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final RandomProvider randomProvider;

    @Autowired
    public SystemAssetService(SystemAssetRepository assetRepository,
                              ScanReportRepository scanReportRepository,
                              VulnerabilityRepository vulnerabilityRepository) {
        this(assetRepository, scanReportRepository, vulnerabilityRepository, new DefaultRandomProvider());
    }

    // Constructor with injected RandomProvider for testing
    public SystemAssetService(SystemAssetRepository assetRepository,
                              ScanReportRepository scanReportRepository,
                              VulnerabilityRepository vulnerabilityRepository,
                              RandomProvider randomProvider) {
        this.assetRepository = assetRepository;
        this.scanReportRepository = scanReportRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.randomProvider = randomProvider;
    }

    /**
     * Execute a security scan on the given asset with realistic probability logic:
     * - 70% chance: No threats found (HEALTHY status)
     * - 30% chance: 1-3 vulnerabilities found (AT_RISK status)
     *
     * If the vulnerability repository is empty, always defaults to HEALTHY (Safe outcome).
     *
     * @param assetId the ID of the asset to scan
     * @throws NotFoundException if the asset does not exist
     */
    public void runScan(Long assetId) {
        SystemAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NotFoundException("SystemAsset not found: " + assetId));

        ScanReport report = new ScanReport();
        report.setAsset(asset);
        report.setTimestamp(Instant.now());

        List<Vulnerability> allVulns = vulnerabilityRepository.findAll();

        // If no vulnerabilities in database, always default to safe/healthy
        if (allVulns.isEmpty()) {
            report.setVulnerabilities(new HashSet<>());
            report.setStatus(ScanReport.Status.COMPLETED);
        } else {
            // 70% chance: scan finds no threats (HEALTHY)
            // 30% chance: scan finds threats (AT_RISK)
            boolean foundThreats = randomProvider.nextInt(100) < 30;

            if (!foundThreats) {
                // Safe outcome: no threats found
                report.setVulnerabilities(new HashSet<>());
                report.setStatus(ScanReport.Status.COMPLETED);
            } else {
                // Vulnerable outcome: find 1-3 random vulnerabilities
                java.util.Collections.shuffle(allVulns);
                int count = 1 + randomProvider.nextInt(3); // 1, 2, or 3
                count = Math.min(count, allVulns.size());
                Set<Vulnerability> foundVulnerabilities = new HashSet<>(allVulns.subList(0, count));
                report.setVulnerabilities(foundVulnerabilities);
                report.setStatus(ScanReport.Status.VULNERABLE);
            }
        }

        scanReportRepository.save(report);
    }

    // === Остальные методы ===

    public List<SystemAssetDto> findAll() {
        return assetRepository.findAll()
                .stream()
                .map(SystemAssetMapper::toDto)
                .collect(Collectors.toList());
    }

    public SystemAssetDto findById(Long id) {
        SystemAsset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SystemAsset not found: " + id));
        return SystemAssetMapper.toDto(asset);
    }

    public SystemAssetDto create(SystemAssetDto dto) {
        SystemAsset asset = SystemAssetMapper.toEntity(dto);
        asset.setId(null);
        return SystemAssetMapper.toDto(assetRepository.save(asset));
    }

    public SystemAssetDto update(Long id, SystemAssetDto dto) {
        SystemAsset existing = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SystemAsset not found: " + id));

        existing.setHostname(dto.getHostname());
        existing.setIpAddress(dto.getIpAddress());
        existing.setOs(dto.getOs());
        existing.setImportanceLevel(dto.getImportanceLevel());

        return SystemAssetMapper.toDto(assetRepository.save(existing));
    }

    public void delete(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new NotFoundException("SystemAsset not found: " + id);
        }
        assetRepository.deleteById(id);
    }
}