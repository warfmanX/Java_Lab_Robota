package com.vulnguard.web.view;

import com.vulnguard.domain.SystemAsset;
import com.vulnguard.dto.SystemAssetDto;
import com.vulnguard.service.SystemAssetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
// Убираем @RequestMapping("/dashboard") с уровня класса, чтобы гибко управлять путями
public class DashboardController {

    private final SystemAssetService systemAssetService;

    public DashboardController(SystemAssetService systemAssetService) {
        this.systemAssetService = systemAssetService;
    }

    // 1. Главная страница (редирект на дашборд)
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    // 2. Сам дашборд
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Получаем список активов.
        // ВАЖНО: Убедись, что в классе SystemAsset есть геттер getImportanceLevel()
        model.addAttribute("assets", systemAssetService.findAll());
        return "dashboard";
    }

    @PostMapping("/dashboard/add-asset")
    public String addAsset(@RequestParam String hostname,
                           @RequestParam String ipAddress,
                           @RequestParam String os,
                           @RequestParam String importanceLevel) {
        
        SystemAssetDto newAsset = new SystemAssetDto();
        newAsset.setHostname(hostname);
        newAsset.setIpAddress(ipAddress);
        newAsset.setOs(os);
        
        try {
            newAsset.setImportanceLevel(SystemAsset.ImportanceLevel.valueOf(importanceLevel));
        } catch (IllegalArgumentException e) {
            // Если пришел некорректный уровень, ставим LOW по умолчанию
            newAsset.setImportanceLevel(SystemAsset.ImportanceLevel.LOW);
        }

        systemAssetService.create(newAsset);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/scan/{id}")
    public String scanAsset(@PathVariable Long id) {
        systemAssetService.runScan(id);
        return "redirect:/dashboard";
    }
}