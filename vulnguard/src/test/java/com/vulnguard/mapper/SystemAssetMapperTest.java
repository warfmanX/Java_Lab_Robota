package com.vulnguard.mapper;

import com.vulnguard.domain.SystemAsset;
import com.vulnguard.dto.SystemAssetDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemAssetMapperTest {

    @Test
    void toDto_and_back_should_preserve_values() {
        SystemAsset asset = new SystemAsset();
        asset.setId(42L);
        asset.setHostname("host");
        asset.setIpAddress("1.2.3.4");
        asset.setOs("Linux");
        asset.setImportanceLevel(SystemAsset.ImportanceLevel.HIGH);

        SystemAssetDto dto = SystemAssetMapper.toDto(asset);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getHostname()).isEqualTo("host");
        assertThat(dto.getImportanceLevel()).isEqualTo(SystemAsset.ImportanceLevel.HIGH);

        SystemAsset roundtripped = SystemAssetMapper.toEntity(dto);
        assertThat(roundtripped.getHostname()).isEqualTo(asset.getHostname());
        assertThat(roundtripped.getIpAddress()).isEqualTo(asset.getIpAddress());
    }
}
