package com.vulnguard.mapper;

import com.vulnguard.domain.SystemAsset;
import com.vulnguard.dto.SystemAssetDto;

public class SystemAssetMapper {

    private SystemAssetMapper() {
        // utility class
    }

    public static SystemAssetDto toDto(SystemAsset asset) {
        if (asset == null) return null;
        SystemAssetDto dto = new SystemAssetDto();
        dto.setId(asset.getId());
        dto.setHostname(asset.getHostname());
        dto.setIpAddress(asset.getIpAddress());
        dto.setOs(asset.getOs());
        dto.setImportanceLevel(asset.getImportanceLevel());
        dto.setCurrentSecurityStatus(asset.getCurrentSecurityStatus());
        return dto;
    }

    public static SystemAsset toEntity(SystemAssetDto dto) {
        if (dto == null) return null;
        SystemAsset asset = new SystemAsset();
        asset.setId(dto.getId());
        asset.setHostname(dto.getHostname());
        asset.setIpAddress(dto.getIpAddress());
        asset.setOs(dto.getOs());
        asset.setImportanceLevel(dto.getImportanceLevel());
        return asset;
    }
}
