package com.vulnguard.service;

import com.vulnguard.domain.ScanReport;
import com.vulnguard.domain.SystemAsset;
import com.vulnguard.domain.Vulnerability;
import com.vulnguard.repository.ScanReportRepository;
import com.vulnguard.repository.SystemAssetRepository;
import com.vulnguard.repository.VulnerabilityRepository;
import com.vulnguard.service.RandomProvider;
import com.vulnguard.web.api.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class SystemAssetServiceTest {

    private ScanReportRepository scanRepo;
    private SystemAssetRepository assetRepo;
    private VulnerabilityRepository vulnRepo;
    private RandomProvider randomProviderMock;
    private SystemAssetService service;

    @BeforeEach
    void setup() {
        scanRepo = Mockito.mock(ScanReportRepository.class);
        assetRepo = Mockito.mock(SystemAssetRepository.class);
        vulnRepo = Mockito.mock(VulnerabilityRepository.class);
        randomProviderMock = Mockito.mock(RandomProvider.class);
        service = new SystemAssetService(assetRepo, scanRepo, vulnRepo, randomProviderMock);
    }

    @Test
    void runScan_should_throw_if_asset_not_found() {
        when(assetRepo.findById(123L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.runScan(123L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SystemAsset not found");
    }

    @Test
    void runScan_with_no_vulnerabilities_saves_empty_completed_report() {
        SystemAsset asset = new SystemAsset();
        asset.setId(5L);
        when(assetRepo.findById(5L)).thenReturn(Optional.of(asset));
        when(vulnRepo.findAll()).thenReturn(Collections.emptyList());

        service.runScan(5L);

        ArgumentCaptor<ScanReport> captor = ArgumentCaptor.forClass(ScanReport.class);
        Mockito.verify(scanRepo).save(captor.capture());
        ScanReport saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ScanReport.Status.COMPLETED);
        assertThat(saved.getVulnerabilities()).isEmpty();
    }

    @Test
    void runScan_with_vulnerabilities_available_safe_outcome_70_percent() {
        // Arrange: 70% probability = randomProvider.nextInt(100) returns >= 30
        SystemAsset asset = new SystemAsset();
        asset.setId(10L);
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        
        Vulnerability v1 = new Vulnerability();
        Vulnerability v2 = new Vulnerability();
        when(vulnRepo.findAll()).thenReturn(new java.util.ArrayList<>(List.of(v1, v2)));
        
        // Mock RandomProvider to return a value >= 30 (safe outcome, no threats found)
        when(randomProviderMock.nextInt(100)).thenReturn(50);

        // Act
        service.runScan(10L);

        // Assert: report should have no vulnerabilities and COMPLETED status
        ArgumentCaptor<ScanReport> captor = ArgumentCaptor.forClass(ScanReport.class);
        Mockito.verify(scanRepo).save(captor.capture());
        ScanReport saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ScanReport.Status.COMPLETED);
        assertThat(saved.getVulnerabilities()).isEmpty();
    }

    @Test
    void runScan_with_vulnerabilities_available_vulnerable_outcome_30_percent() {
        // Arrange: 30% probability = randomProvider.nextInt(100) returns < 30
        SystemAsset asset = new SystemAsset();
        asset.setId(11L);
        when(assetRepo.findById(11L)).thenReturn(Optional.of(asset));
        
        Vulnerability v1 = new Vulnerability();
        Vulnerability v2 = new Vulnerability();
        when(vulnRepo.findAll()).thenReturn(new java.util.ArrayList<>(List.of(v1, v2)));
        
        // Mock RandomProvider to return a value < 30 (vulnerable outcome, threats found)
        when(randomProviderMock.nextInt(100)).thenReturn(15);
        // Mock nextInt(3) to return a deterministic value (for picking 1-3 vulnerabilities)
        when(randomProviderMock.nextInt(3)).thenReturn(1); // This will result in count = 1 + 1 = 2

        // Act
        service.runScan(11L);

        // Assert: report should have vulnerabilities and VULNERABLE status
        ArgumentCaptor<ScanReport> captor = ArgumentCaptor.forClass(ScanReport.class);
        Mockito.verify(scanRepo).save(captor.capture());
        ScanReport saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ScanReport.Status.VULNERABLE);
        assertThat(saved.getVulnerabilities()).isNotEmpty();
        assertThat(saved.getVulnerabilities().size()).isBetween(1, 3);
    }
}