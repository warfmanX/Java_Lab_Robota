package com.vulnguard.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scan_reports")
public class ScanReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false)
    private Instant timestamp;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private SystemAsset asset;

    @ManyToMany
    @JoinTable(
            name = "scan_report_vulnerabilities",
            joinColumns = @JoinColumn(name = "scan_report_id"),
            inverseJoinColumns = @JoinColumn(name = "vulnerability_id")
    )
    private Set<Vulnerability> vulnerabilities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public SystemAsset getAsset() {
        return asset;
    }

    public void setAsset(SystemAsset asset) {
        this.asset = asset;
    }

    public Set<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(Set<Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public enum Status {
        PENDING,
        RUNNING,
        // report finished but one or more vulnerabilities were found
        VULNERABLE,
        COMPLETED,
        FAILED
    }
}

