package com.vulnguard.service;

import com.vulnguard.domain.ScanReport;
import com.vulnguard.domain.SystemAsset;
import com.vulnguard.domain.Vulnerability;
import com.vulnguard.dto.ScanReportDto;
import com.vulnguard.repository.ScanReportRepository;
import com.vulnguard.repository.SystemAssetRepository;
import com.vulnguard.repository.VulnerabilityRepository;
import com.vulnguard.web.api.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ScanReportServiceTest {

    private ScanReportRepository scanRepo;
    private SystemAssetRepository assetRepo;
    private VulnerabilityRepository vulnRepo;
    private ScanReportService service;

    @BeforeEach
    void setup() {
        scanRepo = Mockito.mock(ScanReportRepository.class);
        assetRepo = Mockito.mock(SystemAssetRepository.class);
        vulnRepo = Mockito.mock(VulnerabilityRepository.class);
        service = new ScanReportService(scanRepo, assetRepo, vulnRepo);
    }

    @Test
    void create_should_throw_if_asset_missing() {
        ScanReportDto dto = new ScanReportDto();
        dto.setAssetId(5L);
        dto.setVulnerabilityIds(List.of(1L));

        when(assetRepo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SystemAsset not found");
    }

    @Test
    void create_should_throw_if_vulnerability_missing() {
        ScanReportDto dto = new ScanReportDto();
        dto.setAssetId(1L);
        dto.setVulnerabilityIds(List.of(1L,2L));

        when(assetRepo.findById(1L)).thenReturn(Optional.of(new SystemAsset()));
        when(vulnRepo.findAllById(ArgumentMatchers.any())).thenReturn(Collections.singletonList(new Vulnerability()));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("One or more vulnerabilities not found");
    }

    @Test
    void create_successful_flow() {
        ScanReportDto dto = new ScanReportDto();
        dto.setAssetId(1L);
        dto.setVulnerabilityIds(List.of(1L));
        dto.setStatus(ScanReport.Status.PENDING);
        dto.setTimestamp(Instant.now());

        SystemAsset asset = new SystemAsset();
        asset.setId(1L);
        Vulnerability v = new Vulnerability();
        v.setId(1L);

        when(assetRepo.findById(1L)).thenReturn(Optional.of(asset));
        when(vulnRepo.findAllById(List.of(1L))).thenReturn(List.of(v));
        when(scanRepo.save(ArgumentMatchers.any())).thenAnswer(i -> i.getArgument(0));

        ScanReportDto result = service.create(dto);
        assertThat(result.getAssetId()).isEqualTo(1L);
        assertThat(result.getVulnerabilityIds()).containsExactly(1L);
    }
}
