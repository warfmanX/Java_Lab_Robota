package com.vulnguard.web.api;

import com.vulnguard.dto.ScanReportDto;
import com.vulnguard.service.ScanReportService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scan-reports")
public class ScanReportController {

    private final ScanReportService scanReportService;

    public ScanReportController(ScanReportService scanReportService) {
        this.scanReportService = scanReportService;
    }

    @GetMapping
    public List<ScanReportDto> getAll() {
        return scanReportService.findAll();
    }

    @GetMapping("/{id}")
    public ScanReportDto getById(@PathVariable Long id) {
        return scanReportService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScanReportDto create(@Validated @RequestBody ScanReportDto dto) {
        return scanReportService.create(dto);
    }

    @PutMapping("/{id}")
    public ScanReportDto update(@PathVariable Long id, @Validated @RequestBody ScanReportDto dto) {
        return scanReportService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        scanReportService.delete(id);
    }
}

