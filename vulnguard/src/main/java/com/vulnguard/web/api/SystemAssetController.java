package com.vulnguard.web.api;

import com.vulnguard.dto.SystemAssetDto;
import com.vulnguard.service.SystemAssetService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class SystemAssetController {

    private final SystemAssetService assetService;

    public SystemAssetController(SystemAssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public List<SystemAssetDto> getAll() {
        return assetService.findAll();
    }

    @GetMapping("/{id}")
    public SystemAssetDto getById(@PathVariable Long id) {
        return assetService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SystemAssetDto create(@Validated @RequestBody SystemAssetDto dto) {
        return assetService.create(dto);
    }

    @PutMapping("/{id}")
    public SystemAssetDto update(@PathVariable Long id, @Validated @RequestBody SystemAssetDto dto) {
        return assetService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assetService.delete(id);
    }

    @PostMapping("/{id}/scan")
    public void scan(@PathVariable Long id) {
        assetService.runScan(id);
    }
}

