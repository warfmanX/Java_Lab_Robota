package com.vulnguard.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "system_assets")
public class SystemAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String hostname;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false, length = 100)
    private String os;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance_level", nullable = false, length = 20)
    private ImportanceLevel importanceLevel;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScanReport> scanReports = new HashSet<>();

    // --- Геттеры и Сеттеры ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public ImportanceLevel getImportanceLevel() {
        return importanceLevel;
    }

    public void setImportanceLevel(ImportanceLevel importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public Set<ScanReport> getScanReports() {
        return scanReports;
    }

    public void setScanReports(Set<ScanReport> scanReports) {
        this.scanReports = scanReports;
    }

    // --- Computed security status, derived from latest report ---
    // @Transient говорит Hibernate не искать эту колонку в базе данных
    @Transient
    public String getCurrentSecurityStatus() {
        // determine latest report and inspect vulnerabilities
        return scanReports.stream()
                .max(java.util.Comparator.comparing(ScanReport::getTimestamp, java.util.Comparator.nullsLast(java.time.Instant::compareTo)))
                .map(report -> {
                    // explicit vulnerable status takes precedence
                    if (report.getStatus() == ScanReport.Status.VULNERABLE) {
                        return "AT_RISK";
                    }
                    if (report.getStatus() != ScanReport.Status.COMPLETED) {
                        return "SCAN_" + report.getStatus().name();
                    }
                    // if there were any vulnerabilities at all, consider at risk
                    boolean hasAny = !report.getVulnerabilities().isEmpty();
                    return hasAny ? "AT_RISK" : "HEALTHY";
                })
                .orElse("NOT_SCANNED");
    }

    public enum ImportanceLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}