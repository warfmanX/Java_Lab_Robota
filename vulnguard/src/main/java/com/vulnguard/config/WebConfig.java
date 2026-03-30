package com.vulnguard.config;

import com.vulnguard.domain.ScanReport;
import com.vulnguard.domain.SystemAsset;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToImportanceLevelConverter());
        registry.addConverter(new StringToScanReportStatusConverter());
    }

    private static class StringToImportanceLevelConverter implements Converter<String, SystemAsset.ImportanceLevel> {
        @Override
        public SystemAsset.ImportanceLevel convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }
            try {
                return SystemAsset.ImportanceLevel.valueOf(source.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return null; // will cause validation failure later
            }
        }
    }

    private static class StringToScanReportStatusConverter implements Converter<String, ScanReport.Status> {
        @Override
        public ScanReport.Status convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }
            try {
                return ScanReport.Status.valueOf(source.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }
}
