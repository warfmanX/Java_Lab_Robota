package com.vulnguard.dto;

import com.vulnguard.domain.SystemAsset;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SystemAssetDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String hostname;

    @NotBlank
    @Size(max = 45)
    @Pattern(regexp = "^[0-9a-fA-F:\\.]+$", message = "Must be a valid IP address format")
    private String ipAddress;

    @NotBlank
    @Size(max = 100)
    private String os;

    @NotNull
    private SystemAsset.ImportanceLevel importanceLevel;

    /**
     * Derived field used for the dashboard to indicate current security status.
     */
    private String currentSecurityStatus;

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

    public SystemAsset.ImportanceLevel getImportanceLevel() {
        return importanceLevel;
    }

    public void setImportanceLevel(SystemAsset.ImportanceLevel importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public String getCurrentSecurityStatus() {
        return currentSecurityStatus;
    }

    public void setCurrentSecurityStatus(String currentSecurityStatus) {
        this.currentSecurityStatus = currentSecurityStatus;
    }
}

