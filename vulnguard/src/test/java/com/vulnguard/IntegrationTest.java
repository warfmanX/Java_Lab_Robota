package com.vulnguard;

import com.vulnguard.domain.Vulnerability;
import com.vulnguard.repository.VulnerabilityRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class IntegrationTest {

    // only start if Docker is present so tests can run even without it
    public static PostgreSQLContainer<?> postgres;
    static {
        if (org.testcontainers.DockerClientFactory.instance().isDockerAvailable()) {
            postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("vulnguard")
                    .withUsername("postgres")
                    .withPassword("postgres");
            postgres.start();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (postgres != null) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        }
    }

    @Autowired
    private VulnerabilityRepository vulnerabilityRepository;

    @Test
    public void flywayRunsAndRepositoryOperates() {
        assumeTrue(org.testcontainers.DockerClientFactory.instance().isDockerAvailable(), "Docker not available");
        // after context start Flyway should have applied migrations
        assertEquals(0, vulnerabilityRepository.count());

        Vulnerability v = new Vulnerability();
        v.setTitle("integration-test");
        v.setDescription("desc");
        v.setSeverityScore(BigDecimal.ZERO);
        v.setPublishedDate(LocalDate.now());
        vulnerabilityRepository.save(v);

        assertEquals(1, vulnerabilityRepository.count());
    }
}
