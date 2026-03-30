package com.vulnguard.web.api;

import com.vulnguard.service.VulnerabilityScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {

    private static final Logger log = LoggerFactory.getLogger(ScraperController.class);

    private final VulnerabilityScraperService scraperService;

    public ScraperController(VulnerabilityScraperService scraperService) {
        this.scraperService = scraperService;
    }

    /**
     * Manual trigger endpoint for testing the scraper.
     *
     * POST /api/scraper/trigger
     */
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerScrape() {
        Map<String, Object> body = new HashMap<>();
        try {
            int created = scraperService.scrapeAndPersist();
            body.put("timestamp", Instant.now().toString());
            body.put("created", created);
            body.put("status", "OK");

            log.info("Manual vulnerability scrape triggered via API. New items saved: {}", created);

            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (Exception ex) {
            log.error("Manual vulnerability scrape failed", ex);
            body.put("timestamp", Instant.now().toString());
            body.put("status", "ERROR");
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}